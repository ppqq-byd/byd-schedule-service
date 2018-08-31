package com.ora.blockchain.service.blockscanner.impl;


import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.mybatis.entity.common.ScanCursor;
import com.ora.blockchain.mybatis.mapper.common.ScanCursorMapper;
import com.ora.blockchain.service.blockscanner.IBlockScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
public abstract class BlockScanner implements IBlockScanner {


    /**
     * 递归回溯 直到分叉前
     * @param needScanBlock
     */
    private void recursionProcessErrorBlock(Long needScanBlock,String coinType) throws Exception {
        //因为这一个块是孤立块 所以删除该块 并且更改这个块所属的tx相应的状态
        deleteBlockAndUpdateTx(needScanBlock);

        if(verifyIsolatedBlock(needScanBlock)){

            recursionProcessErrorBlock(needScanBlock-1,CoinType.getDatabase(coinType));
        }
    }


    @Override
    @Transactional
    public void scanBlock(Long initBlockHeight,String coinType) throws Exception {
        Long needScanBlock = getNeedScanBlockHeight(initBlockHeight);
        System.out.println("*********************************needScanBlock:"+needScanBlock);
        //如果已经是最新块了 那么这次不用扫描了
        if(isNeedScanHeightLasted(needScanBlock)){
            return;
        }
        //如果是孤块 则设置游标 -1 从上个块重新扫描
        if(verifyIsolatedBlock(needScanBlock)){
            recursionProcessErrorBlock(needScanBlock-1,coinType);
            return;
        }

        //将块信息和tx同步到数据库
        syncBlockAndTx(needScanBlock);
    }



    @Override
    @Transactional
    public void updateAccount(String coinType) {

        updateAccountBalanceByConfirmTx(0L);


    }

    /**
     *  判断当前待扫描的块是不是已经是最新的块了
     * @param needScanBlock
     * @return
     */
    public abstract boolean isNeedScanHeightLasted(Long needScanBlock) throws IOException;

    /**
     * 删除块以及更新tx的状态
     * @param initBlockHeight
     */
    public abstract void deleteBlockAndUpdateTx(Long initBlockHeight);

    /**
     * 根据初始化的块高度 DB中的块高度 节点上的块高度
     * 获取一个当前需要扫描的块高度
     * @param initBlockHeight
     * @return
     */
    public abstract Long getNeedScanBlockHeight(Long initBlockHeight);

    /**
     * 判断孤立块
     * @return
     */
    public abstract boolean verifyIsolatedBlock(Long needScanBlock) throws Exception;



    /**
     * 将块信息和tx同步到数据库
     */
    public abstract void syncBlockAndTx(Long blockHeight) throws Exception;


    /**
     * 更新账户余额的数值
     * @param lastedBlock
     */
    public abstract void updateAccountBalanceByConfirmTx(Long lastedBlock);

    /**
     * 获取最新的块高度
     * coinType 币种
     * @return
     */
    public abstract Long getNeedScanAccountBlanceBlock(String coinType);

}
