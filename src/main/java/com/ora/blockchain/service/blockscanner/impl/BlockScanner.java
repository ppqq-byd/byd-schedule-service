package com.ora.blockchain.service.blockscanner.impl;


import com.ora.blockchain.service.blockscanner.IBlockScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public abstract class BlockScanner implements IBlockScanner {


    @Override
    public void scanBlock(Long initBlockHeight) {
        Long needScanBlock = getNeedScanBlockHeight(initBlockHeight);

        //如果是孤块 则设置游标 -1 从上个块重新扫描
        if(verifyIsolatedBlock(needScanBlock)){
            setScannerCursor(needScanBlock-1);
            return;
        }

        //将块信息写入数据库
        insertBlock(needScanBlock);
        //将交易信息写入数据库
        syncTransaction(needScanBlock);
    }

    @Override
    public void updateAccount() {

    }

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
    public abstract boolean verifyIsolatedBlock(Long needScanBlock);

    /**
     * 设置扫描的块高度游标
     */
    public abstract void setScannerCursor(Long blockHeight);

    /**
     * 写块的信息 入数据库
     */
    public abstract void insertBlock(Long blockHeight);

    /**
     * 同步交易信息
     * @param blockHeight
     */
    public abstract void syncTransaction(Long blockHeight);
}
