package com.ora.blockchain.service.blockscanner.impl.btcfamily;

import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.blockscanner.impl.BlockScanner;
import com.ora.blockchain.service.rpc.IRpcService;

public abstract class BtcfamilyBlockScanner extends BlockScanner {

    @Override
    public void deleteBlockAndUpdateTx(Long blockHeight) {
        if (null != blockHeight) {
            getBlockService().deleteByHeight(getCoinType(),blockHeight);
        }
    }

    @Override
    public Long getNeedScanBlockHeight(Long initBlockHeight) {
        Block block = getBlockService().queryLastBlock(getCoinType());
        return null != block ? block.getHeight().longValue() + 1L : initBlockHeight;
    }

    @Override
    public boolean verifyIsolatedBlock(Long needScanBlock) {
        Block paramBlock = getRpcService().getBlock(needScanBlock);
        if (null == paramBlock) {
            return false;
        }
        Block dbBlock = getBlockService().queryLastBlock(getCoinType());
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
        getBlockService().insertBlock(getCoinType(),block);
    }

    @Override
    public boolean isNeedScanHeightLasted(Long needScanBlock) {
        Long height = getRpcService().getBestBlockHeight();
        return null != needScanBlock && null != height && needScanBlock.longValue() > height.longValue();
    }

    public abstract IBlockService getBlockService();
    public abstract IRpcService getRpcService();
    public abstract String getCoinType();
}
