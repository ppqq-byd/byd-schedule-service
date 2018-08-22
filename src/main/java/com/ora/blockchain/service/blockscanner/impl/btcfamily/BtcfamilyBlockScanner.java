package com.ora.blockchain.service.blockscanner.impl.btcfamily;

import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBalance;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBind;
import com.ora.blockchain.mybatis.mapper.output.OutputMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBalanceMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBindMapper;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.blockscanner.impl.BlockScanner;
import com.ora.blockchain.service.rpc.IRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public abstract class BtcfamilyBlockScanner extends BlockScanner {
    @Autowired
    private WalletAccountBindMapper bindMapper;
    @Autowired
    private WalletAccountBalanceMapper balanceMapper;
    @Autowired
    private OutputMapper outputMapper;

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

    }

    @Override
    public Long getNeedScanAccountBlanceBlock(String coinType) {

        return null;

    }
/*
    @Override
    public List<WalletAccountBind> getWalletAccountBindByCoinType(String coinType) {
        //TODO 按block找出待更新的用户
        return bindMapper.queryWalletAccountBindByCoinType(coinType);
    }

    @Override
    public void updateAccountBalance(List<WalletAccountBind> list) {
        //TODO frozenBalance 已发出的金额冻结
        //TODO totalBalance 经过N个块确认的金额，包含已发出的金额
        if(null == list || list.isEmpty())
            return;

        List<WalletAccountBalance> balanceList = new ArrayList<>();
        Block lastBlock = getBlockService().queryLastBlock(CoinType.getDatabase(getCoinType()));
        list.forEach(t->{
            List<Output> utxoList = outputMapper.queryUTXOList(CoinType.getDatabase(getCoinType()),t.getAccountId());
            WalletAccountBalance balance = new WalletAccountBalance();
            balance.setAccountId(t.getAccountId());
            balance.setCoinType(t.getCoinType());
            if(null != utxoList){
                Long frozenBalance = utxoList.stream().map(output->{
                    if (null != lastBlock)
                        output.setConfirmations(lastBlock.getHeight().longValue() - output.getHeight().longValue() + 1);
                    return output;
                }).filter(output -> (output.getCoinbase() == 0 && output.getConfirmations() < getIndispensableConfirmations()) || (output.getCoinbase() == 1 && output.getConfirmations() < getIndispensableCoinbaseConfirmations()))
                .mapToLong(Output::getValueSat).sum();
                balance.setFrozenBalance(null == frozenBalance ? 0 : frozenBalance);
                Long totalBalance = utxoList.stream().mapToLong(Output::getValueSat).sum();
                balance.setTotalBalance(null == totalBalance ? 0 : totalBalance);
            }
            balanceList.add(balance);
        });

        if(null != balanceList && !balanceList.isEmpty())
            balanceMapper.updateBatch(balanceList);
    }
*/
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
