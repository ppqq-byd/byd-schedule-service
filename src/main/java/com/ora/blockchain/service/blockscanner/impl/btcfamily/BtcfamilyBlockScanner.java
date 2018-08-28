package com.ora.blockchain.service.blockscanner.impl.btcfamily;

import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.mybatis.entity.common.ScanCursor;
import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBalance;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBind;
import com.ora.blockchain.mybatis.mapper.block.BlockMapper;
import com.ora.blockchain.mybatis.mapper.common.ScanCursorMapper;
import com.ora.blockchain.mybatis.mapper.input.InputMapper;
import com.ora.blockchain.mybatis.mapper.output.OutputMapper;
import com.ora.blockchain.mybatis.mapper.transaction.TransactionMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBalanceMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBindMapper;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.blockscanner.impl.BlockScanner;
import com.ora.blockchain.service.rpc.IRpcService;
import org.apache.commons.lang3.StringUtils;
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
    private ScanCursorMapper cursorMapper;
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
    public void updateAccountBalanceByConfirmTx(Long lastedBlock) {
        if (null == lastedBlock || lastedBlock.longValue() <= 0)
            return;

        String database = CoinType.getDatabase(getCoinType());
        Block lastBlock = blockMapper.queryLastBlock(database);
        if (null == lastBlock)
            return;

        List<Long> accountList = getWalletAccoutByBlock(lastedBlock);
        if (null == accountList || accountList.isEmpty())
            return;

        accountList = accountList.stream().distinct().collect(Collectors.toList());
        List<WalletAccountBalance> totalBalance = outputMapper.queryTotalBalance(database, accountList, lastBlock.getHeight() - getIndispensableConfirmations() + 1, lastBlock.getHeight() - getIndispensableCoinbaseConfirmations() + 1);
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

    public List<Long> getWalletAccoutByBlock(Long blockHeight){
        if(null == blockHeight || blockHeight.longValue() <= 0)
            return null;

        String database = CoinType.getDatabase(getCoinType());

        Block block = blockMapper.queryByBlockHeight(database, blockHeight - getIndispensableConfirmations() + 1);
        List<Transaction> transList = new ArrayList<>();
        if(null != block){
            //已被确认的非coinbase交易
            transList.addAll(transMapper.queryTransactionListByBlockHash(database,block.getBlockHash(),0));
        }

        Block coinbaseBlock = blockMapper.queryByBlockHeight(database,blockHeight - getIndispensableCoinbaseConfirmations() + 1);
        if(null != coinbaseBlock){
            //已被确认的coinbase交易
            transList.addAll(transMapper.queryTransactionListByBlockHash(database,coinbaseBlock.getBlockHash(),1));
        }

        if(null == transList || transList.isEmpty())
            return null;

        List<String> txidList = transList.stream().map((Transaction t)->{return t.getTxid();}).collect(Collectors.toList());
        List<Long> accountList = new ArrayList<>();
        List<Long> outputList = outputMapper.queryAccountByTransactionTxid(database,txidList);
        if(null != outputList && !outputList.isEmpty())
            accountList.addAll(outputList);
        List<Long> inputList = inputMapper.queryAccountByTransactionTxid(database,txidList);
        if(null != inputList && !inputList.isEmpty())
            accountList.addAll(inputList);

        return accountList;
    }

    @Override
    public Long getNeedScanAccountBlanceBlock(String coinType) {
        ScanCursor cursor = cursorMapper.getNotConfirmScanCursor(CoinType.getDatabase(getCoinType()));
        return null != cursor ? cursor.getCurrentBlock() : null;

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
