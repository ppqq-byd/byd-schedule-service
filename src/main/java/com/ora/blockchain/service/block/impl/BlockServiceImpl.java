package com.ora.blockchain.service.block.impl;

import com.ora.blockchain.constants.OutputStatus;
import com.ora.blockchain.constants.TxDireStatus;
import com.ora.blockchain.constants.TxStatus;
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
import com.ora.blockchain.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
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
                return null == output ? null : output.getScriptPubKeyAddresses();
            }).collect(Collectors.toList());
            if (CollectionUtils.containsAny(addressSet, outputAddrList) || CollectionUtils.containsAny(addressSet, inputAddrList)) {
                transList.add(t);
            }
        });
        return transList;
    }

    @Transactional
    public void insertBlockTransaction(String database,final List<Transaction> paramTransactionList) {
        if(null == paramTransactionList || paramTransactionList.isEmpty())
            return;

        List<String> txidList = paramTransactionList.stream().map((Transaction t) ->{return t.getTxid();}).collect(Collectors.toList());
        List<Transaction> updateList = transMapper.queryTransactionListByTxid(database,txidList);
        if(null != updateList && !updateList.isEmpty()){
            updateList.forEach((Transaction t)->{
                t.setHeight(paramTransactionList.get(0).getHeight());
                t.setBlockHash(paramTransactionList.get(0).getBlockHash());
                if(t.getTransStatus()==TxStatus.ISOLATED.ordinal()){
                    t.setTransStatus(TxStatus.ISOLATEDCONRIMING.ordinal());
                }else{
                    t.setTransStatus(TxStatus.CONFIRMING.ordinal());
                }
            });
            transMapper.updateTransactionList(database,updateList);
            paramTransactionList.removeAll(updateList);
        }

        if (null != paramTransactionList && !paramTransactionList.isEmpty()) {
            List<Output> outputList = new ArrayList<>();
            List<Input> inputList = new ArrayList<>();
            for (Transaction t : paramTransactionList) {
                outputList.addAll(t.getOutputList());
                inputList.addAll(t.getInputList());
            }
            //Transaction默认状态为CONFIRMING
            transMapper.insertTransactionList(database, paramTransactionList);
            outputMapper.insertOutputList(database, outputList);
            inputMapper.insertInputList(database, inputList);
        }
    }
    private List<String> getAddress(String database,List<Transaction> transactionList){
        if(null == transactionList || transactionList.isEmpty())
            return null;

        List<Output> outputList = new ArrayList<>();
        List<Input> inputList = new ArrayList<>();
        for(Transaction t : transactionList){
            if(null != t && null != t.getOutputList() && !t.getOutputList().isEmpty()){
                outputList.addAll(t.getOutputList());
            }
            if(null != t && null != t.getInputList() && !t.getInputList().isEmpty()){
                inputList.addAll(t.getInputList());
            }
        }
        List<String> resList = new ArrayList<>();
        List<String> outputAddrList = outputList.stream().map(Output :: getScriptPubKeyAddresses).collect(Collectors.toList());
        if(null != outputAddrList)
            resList.addAll(outputAddrList);
        List<String> inputAddrList = outputMapper.queryAddressByTransactionTxid(database,inputList.stream().map(Input::getTxid).collect(Collectors.toList()));
        if(null != inputAddrList)
            resList.addAll(inputAddrList);

        return resList;
    }

    @Override
    @Transactional
    public void insertBlock(String database, Block block) {
        if(null == block || StringUtils.isBlank(database))
            return;
        blockMapper.insertBlock(database,block);

        List<Transaction> paramTransactionList = getRpcService().getTransactionList(block.getBlockHash());

        List<String> addressList = getAddress(database,paramTransactionList);
        if(null == addressList || addressList.isEmpty()){
            return;
        }

        //当前块包含的平台用户
        List<Wallet> walletList = walletMapper.queryWalletByAddress(addressList);
        if(null == walletList || walletList.isEmpty()){
            return;
        }

        Map<String,Long> walletMap = walletList.stream().collect(Collectors.toMap(Wallet::getAddress,Wallet::getWalletAccountId));
        Set<String> addressSet = walletMap.keySet();
        List<Transaction> oraTransactinList = filterTransaction(database,paramTransactionList,addressSet);

        if (null != oraTransactinList && !oraTransactinList.isEmpty()) {
            oraTransactinList.forEach((Transaction t) -> {
                t.setHeight(block.getHeight());
                t.getOutputList().forEach((Output output) -> {
                    if(null == output.getValueSat())
                        output.setValueSat(convertToSatoshis(output.getValue()));
                    output.setWalletAccountId(walletMap.get(output.getScriptPubKeyAddresses()));
                    //当前交易产生的UTXO状态为“不可使用”
                    output.setStatus(OutputStatus.INVALID.ordinal());
                });
                List<String> outputAddrList = t.getOutputList().stream().map(Output::getScriptPubKeyAddresses).collect(Collectors.toList());
                List<String> inputAddrList = new ArrayList<>();
                t.getInputList().forEach((Input input) -> {
                    Output output = outputMapper.queryOutputByPrimary(database, input.getTxid(), input.getVout());
                    if(null != output){
                        inputAddrList.add(output.getScriptPubKeyAddresses());
                        input.setAddress(output.getScriptPubKeyAddresses());
                    }
                    input.setWalletAccountId(null == output ? null : output.getWalletAccountId());
                    //当前交易使用的UTXO状态为“使用中”
                    outputMapper.updateOutput(database, OutputStatus.USING.ordinal(), input.getTxid(), input.getVout());
                });
                //删除找零地址
                outputAddrList = (List<String>) CollectionUtils.removeAll(outputAddrList,inputAddrList);

                //vin包含平台地址且vout不包含平台址，trans_dire为“内转外”
                if(null != outputAddrList && !outputAddrList.isEmpty() && CollectionUtils.containsAny(addressSet,inputAddrList) && !CollectionUtils.containsAny(addressSet,outputAddrList)){
                    t.setTransDire(TxDireStatus.OUTPUT.ordinal());
                    //vin不包含平台地址且vout包含平台地址，trans_dire为“外转内”
                }else if(null != outputAddrList && !outputAddrList.isEmpty() && !CollectionUtils.containsAny(addressSet,inputAddrList) && CollectionUtils.containsAny(addressSet,outputAddrList)){
                    t.setTransDire(TxDireStatus.INPUT.ordinal());
                    //vin和vout同时包含平台地址，trans_dire为“内转内”，不存在vin和vout同时不包含的情况
                }else{
                    t.setTransDire(TxDireStatus.INTERNAL.ordinal());
                }
            });
            insertBlockTransaction(database,oraTransactinList);
        }
    }

    @Override
    @Transactional
    public void deleteByHeight(String database, Long blockHeight) {
        Block block = blockMapper.queryByBlockHeight(database, blockHeight);
        if(null == block)
            return;

        //修改vin & vout的状态
        List<Transaction> transList = transMapper.queryTransactionListByBlockHash(database,block.getBlockHash());
        if(null != transList && !transList.isEmpty()){
            List<String> txidList = transList.stream().map(Transaction::getTxid).collect(Collectors.toList());
            // 修改所有input为“使用中”
            List<Input> inputList = inputMapper.queryInputByTxid(database,txidList);
            List<Output> outputList = new ArrayList<>();
            if(null != inputList && !inputList.isEmpty()){
                inputList.forEach((Input input)->{
                    Output output = new Output();
                    output.setN(input.getVout());
                    output.setTransactionTxid(input.getTxid());
                    outputList.add(output);
                });
                outputMapper.updateOutputBatch(database,OutputStatus.USING.ordinal(),outputList);
            }

            // 修改所有output为“不可用”
            if(null != txidList && !txidList.isEmpty()){
                outputMapper.updateOutputByTxid(database,OutputStatus.INVALID.ordinal(),txidList);
            }
        }

        //物理删除block表记录
        blockMapper.deleteBlockByBlockHash(database,block.getBlockHash());
        //逻辑删除Transaction，修改Transaction记录block_hash 为NULL,height为NULL,trans_tatus为4（孤立状态）
        transMapper.deleteTransactionByBlockHash(database,block.getBlockHash());
    }

    @Override
    @Transactional
    public Block queryLastBlock(String database){
        return blockMapper.queryLastBlock(database);
    }

    protected BigInteger convertToSatoshis(Double value) {
        return new BigDecimal(value.toString()).multiply(new BigDecimal(Utils.COIN.longValue())).toBigInteger();
    }
    public abstract IRpcService getRpcService();
}
