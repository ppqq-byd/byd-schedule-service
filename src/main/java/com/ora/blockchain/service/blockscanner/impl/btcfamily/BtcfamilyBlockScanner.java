package com.ora.blockchain.service.blockscanner.impl.btcfamily;

import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.constants.OutputStatus;
import com.ora.blockchain.constants.TxStatus;
import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.mybatis.entity.input.Input;
import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBalance;
import com.ora.blockchain.mybatis.mapper.block.BlockMapper;

import com.ora.blockchain.mybatis.mapper.input.InputMapper;
import com.ora.blockchain.mybatis.mapper.output.OutputMapper;
import com.ora.blockchain.mybatis.mapper.transaction.TransactionMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBalanceMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBindMapper;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.blockscanner.impl.BlockScanner;
import com.ora.blockchain.service.rpc.IRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public abstract class BtcfamilyBlockScanner extends BlockScanner {
    @Autowired
    private WalletAccountBindMapper bindMapper;
    @Autowired
    private WalletAccountBalanceMapper balanceMapper;
    @Autowired
    private OutputMapper outputMapper;
    @Autowired
    private InputMapper inputMapper;

    @Autowired
    private BlockMapper blockMapper;
    @Autowired
    private TransactionMapper transMapper;

    @Override
    public void deleteBlockAndUpdateTx(Long blockHeight) {
        if (null != blockHeight) {
            getBlockService().deleteByHeight(CoinType.getDatabase(getCoinType()),blockHeight);
        }
    }

    @Override
    public Long getNeedScanBlockHeight(Long initBlockHeight) {
        Block block = getBlockService().queryLastBlock(CoinType.getDatabase(getCoinType()));
        return null != block ? block.getHeight().longValue() + 1L : initBlockHeight;
    }

    @Override
    public boolean verifyIsolatedBlock(Long needScanBlock) {
        Block paramBlock = getRpcService().getBlock(needScanBlock);
        if (null == paramBlock) {
            return false;
        }
        Block dbBlock = getBlockService().queryLastBlock(CoinType.getDatabase(getCoinType()));
        if (null != dbBlock && !paramBlock.getPreviousBlockHash().equals(dbBlock.getBlockHash())) {
            return true;
        }
        return false;
    }

    @Override
    public void syncBlockAndTx(Long blockHeight) {
        Block block = getRpcService().getBlock(blockHeight);
        if(null == block)
            return;
        getBlockService().insertBlock(CoinType.getDatabase(getCoinType()),block);
    }

    @Override
    public boolean isNeedScanHeightLasted(Long needScanBlock) {
        Long height = getRpcService().getBestBlockHeight();
        return null != needScanBlock && null != height && needScanBlock.longValue() > height.longValue();
    }

    @Override
    public void updateAccountBalanceByConfirmTx() {
        String database = CoinType.getDatabase(getCoinType());
        Block lastBlock = blockMapper.queryLastBlock(database);
        if (null == lastBlock)
            return ;

        List<Long> accountList = getNeedUpdateWalletAccout(lastBlock);
        if (null == accountList || accountList.isEmpty())
            return;

        accountList = accountList.stream().distinct().collect(Collectors.toList());
        List<WalletAccountBalance> totalBalance = outputMapper.queryTotalBalance(database, accountList);
        if (null != totalBalance && !totalBalance.isEmpty()) {
            Map<Long, BigInteger> map = new HashMap<>();
            totalBalance.forEach((WalletAccountBalance b) -> {
                map.put(b.getAccountId(), null != map.get(b.getAccountId()) ? map.get(b.getAccountId()).add(b.getTotalBalance()) : b.getTotalBalance());
            });
            updateAccountBatch(map);
        }
    }

    public void updateAccountBatch(Map<Long, BigInteger> balanceMap) {
        if (null == balanceMap || balanceMap.isEmpty())
            return;
        Set<Long> keySet = balanceMap.keySet();
        List<WalletAccountBalance> balanceList = keySet.stream().map((Long accountId) -> {
            WalletAccountBalance wab = new WalletAccountBalance();
            wab.setAccountId(accountId);
            wab.setTotalBalance(balanceMap.get(accountId));
            wab.setCoinType(getCoinType());
            return wab;
        }).collect(Collectors.toList());

        if (null != balanceList && !balanceList.isEmpty())
            balanceMapper.updateBatch(balanceList);
    }

    //更新交易状态并返回accountId
    public List<Long> getNeedUpdateWalletAccout(Block lastBlock) {
        if (null == lastBlock)
            return null;
        String database = CoinType.getDatabase(getCoinType());

        //查询出所有状态为“确认中”的交易
        List<Integer> transStatusList = new ArrayList<>();
        transStatusList.add(TxStatus.CONFIRMING.ordinal());
        transStatusList.add(TxStatus.ISOLATEDCONRIMING.ordinal());
        List<Transaction> transList = transMapper.queryTransactionListByTransStatus(database, transStatusList);
        if (null == transList || transList.isEmpty())
            return null;

        List<Transaction> completeTransList = new ArrayList<>();
        List<Long> accoutList = new ArrayList<>();
        for(Transaction t:transList){
            List<String> txidList = new ArrayList<>();
            txidList.add(t.getTxid());
            //coinbase交易
            if (null != t.getCoinbase() && t.getCoinbase().intValue() == Transaction.TRANSACTION_COINBASE) {
                if (lastBlock.getHeight() - t.getHeight() + 1 >= getIndispensableCoinbaseConfirmations()) {
                    //coinbase交易找出所有vout的用户
                    accoutList.addAll(outputMapper.queryAccountByTransactionTxid(database, txidList));
                    //coinbase交易完成
                    t.setTransStatus(TxStatus.COMPLETE.ordinal());
                    completeTransList.add(t);
                }
                //普通交易
            } else {
                if (lastBlock.getHeight() - t.getHeight() + 1 >= getIndispensableConfirmations()) {
                    accoutList.addAll(outputMapper.queryAccountByTransactionTxid(database, txidList));
                    //vin包含的用户
                    accoutList.addAll(inputMapper.queryAccountByTransactionTxid(database, txidList));
                    //普通交易完成
                    t.setTransStatus(TxStatus.COMPLETE.ordinal());
                    completeTransList.add(t);
                }
            }
        }

        if (null != completeTransList && !completeTransList.isEmpty()) {
            //修改状态为已完成
            transMapper.updateTransactionList(database, completeTransList);

            List<String> txidList = completeTransList.stream().map(Transaction::getTxid).collect(Collectors.toList());
            // 修改所有input为“使用中”
            List<Input> inputList = inputMapper.queryInputByTxid(database,txidList);
            List<Output> outputList = new ArrayList<>();
            if(null != inputList && !inputList.isEmpty()){
                inputList.forEach((Input input)->{
                    if(null != input){
                        Output output = new Output();
                        output.setN(input.getVout());
                        output.setTransactionTxid(input.getTxid());
                        outputList.add(output);
                    }
                });
                if(null != outputList && !outputList.isEmpty())
                    outputMapper.updateOutputBatch(database,OutputStatus.SPENT.ordinal(),outputList);
            }

            // 修改所有output为“不可用”
            if(null != txidList && !txidList.isEmpty()){
                outputMapper.updateOutputByTxid(database,OutputStatus.VALID.ordinal(),txidList);
            }
        }
        return accoutList;
    }


    protected int getIndispensableConfirmations(){
        return 6;
    }

    protected  int getIndispensableCoinbaseConfirmations(){
        return 100;
    }
    public abstract IBlockService getBlockService();
    public abstract IRpcService getRpcService();
    public abstract String getCoinType();
}
