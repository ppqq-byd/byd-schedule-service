package com.ora.blockchain.service.block.impl;

import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.mybatis.entity.input.Input;
import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import com.ora.blockchain.mybatis.entity.wallet.Wallet;
import com.ora.blockchain.mybatis.mapper.block.BlockMapper;
import com.ora.blockchain.mybatis.mapper.input.InputMapper;
import com.ora.blockchain.mybatis.mapper.output.OutputMapper;
import com.ora.blockchain.mybatis.mapper.transaction.TransactionMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletMapper;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.rpc.IRpcService;
import com.ora.blockchain.utils.BlockchainUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public abstract class BlockServiceImpl implements IBlockService {

    @Autowired
    private BlockMapper blockMapper;
    @Autowired
    private TransactionMapper transMapper;
    @Autowired
    private InputMapper inputMapper;
    @Autowired
    private OutputMapper outputMapper;
    @Autowired
    private WalletMapper walletMapper;

    private List<Transaction> filterTransaction(String database, List<Transaction> blockchainTrans, Set<String> addressSet) {
        if (null == blockchainTrans || blockchainTrans.isEmpty() || null == addressSet || addressSet.isEmpty()) {
            return null;
        }

        List<Transaction> transList = new ArrayList<>();
        blockchainTrans.forEach((Transaction t) -> {
            List<String> outputAddrList = t.getOutputList().stream().map(Output::getScriptPubKeyAddresses).collect(Collectors.toList());
            List<String> inputAddrList = t.getInputList().stream().map(input -> {
                Output output = outputMapper.queryOutputByPrimary(database, input.getTxid(), input.getVout());
                input.setAddress(null == output ? null : output.getScriptPubKeyAddresses());
                return input.getAddress();
            }).collect(Collectors.toList());
            if (CollectionUtils.containsAny(addressSet, outputAddrList) || CollectionUtils.containsAny(addressSet, inputAddrList)) {
                transList.add(t);
            }
        });
        return transList;
    }

    @Transactional
    public void insertBlockTransaction(String database,String blockHash,List<Transaction> transactionList) {
        if(null == transactionList || transactionList.isEmpty())
            return;
        List<Output> outputList = new ArrayList<>();
        List<Input> inputList = new ArrayList<>();
        for(Transaction  t:transactionList){
            outputList.addAll(t.getOutputList());
            inputList.addAll(t.getInputList());
        }
        transMapper.insertTransactionList(database,transactionList);
        outputMapper.insertOutputList(database,outputList);
        inputMapper.insertInputList(database,inputList);
        // 修改output表状态为无效
        if(null != inputList && !inputList.isEmpty()){
            List<Output> oList = new ArrayList<>();
            inputList.forEach((Input input) -> {
                if(StringUtils.isBlank(input.getCoinbase())){
                    Output output = new Output();
                    output.setTransactionTxid(input.getTxid());
                    output.setN(input.getVout());
                    oList.add(output);
                }
            });
            if(null != oList && !oList.isEmpty())
                outputMapper.updateOutputBatch(database,Output.STATUS_SPENT,oList);
        }
    }

    @Override
    @Transactional
    public void insertBlock(String database, Block block) {
        if(null == block || StringUtils.isBlank(database))
            return;
        blockMapper.insertBlock(database,block);

        List<Transaction> paramTransactionList = getRpcService().getTransactionList(block.getBlockHash());
        List<String> addressList = BlockchainUtil.getAddress(paramTransactionList);
        if(null == addressList || addressList.isEmpty()){
            return;
        }

        List<Wallet> walletList = walletMapper.queryWalletByAddress(addressList);
        if(null == walletList || walletList.isEmpty()){
            return;
        }

        Map<String,Long> walletMap = walletList.stream().collect(Collectors.toMap(Wallet::getAddress,Wallet::getWalletAccountId));
        List<Transaction> oraTransactinList = filterTransaction(database,paramTransactionList,walletMap.keySet());

        if (null != oraTransactinList && !oraTransactinList.isEmpty()) {
            oraTransactinList.forEach((Transaction t) -> {
                t.setHeight(block.getHeight());
                if (null != t.getOutputList() && !t.getOutputList().isEmpty()) {
                    t.getOutputList().forEach((Output output) -> {
                        output.setWalletAccountId(walletMap.get(output.getScriptPubKeyAddresses()));
                    });
                }
                if (null != t.getInputList() && !t.getInputList().isEmpty()) {
                    t.getInputList().forEach((Input input) -> {
                        Output output = outputMapper.queryOutputByPrimary(database, input.getTransactionTxid(), input.getVout());
                        input.setAddress(null == output ? null : output.getScriptPubKeyAddresses());
                        input.setWalletAccountId(null == output ? null : output.getWalletAccountId());
                        outputMapper.updateOutput(database, Output.STATUS_SPENT, input.getTransactionTxid(), input.getVout());
                    });
                }
            });
            insertBlockTransaction(database,block.getBlockHash(),oraTransactinList);
        }
    }

    @Override
    @Transactional
    public void deleteByHeight(String database, Long blockHeight) {
        Block block = blockMapper.queryByBlockHeight(database, blockHeight);
        if(null == block)
            return;
        List<Input> iList = inputMapper.queryByBlockHash(database,block.getBlockHash());
        if(null != iList && !iList.isEmpty()){
            List<Output> outputList = new ArrayList<>();
            iList.forEach((Input input) -> {
                if(StringUtils.isBlank(input.getCoinbase())){
                    Output output = new Output();
                    output.setTransactionTxid(input.getTxid());
                    output.setN(input.getVout());
                    outputList.add(output);
                }
            });
            // 修改output表状态为有效
            if(null != outputList && !outputList.isEmpty())
                outputMapper.updateOutputBatch(database,Output.STATUS_UNSPENT,outputList);
        }

        blockMapper.deleteBlockByBlockHash(database,block.getBlockHash());
        transMapper.deleteTransactionByBlockHash(database,block.getBlockHash());
        inputMapper.deleteInput(database,block.getBlockHash());
        outputMapper.deleteOutput(database,block.getBlockHash());
    }

    @Override
    @Transactional
    public Block queryLastBlock(String database){
        return blockMapper.queryLastBlock(database);
    }

    public abstract IRpcService getRpcService();
}
