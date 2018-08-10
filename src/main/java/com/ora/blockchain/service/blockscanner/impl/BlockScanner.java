package com.ora.blockchain.service.blockscanner.impl;


import com.ora.blockchain.service.blockscanner.IBlockScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public abstract class BlockScanner implements IBlockScanner {

    private void recursionProcessErrorBlock(Long needScanBlock){
        //因为这一个块是孤立块 所以删除该块 并且更改这个块所属的tx相应的状态
        deleteBlockAndUpdateTx(needScanBlock);

        if(verifyIsolatedBlock(needScanBlock)){

            recursionProcessErrorBlock(needScanBlock-1);
        }
    }


    @Override
    public void scanBlock(Long initBlockHeight) {
        Long needScanBlock = getNeedScanBlockHeight(initBlockHeight);

        //如果是孤块 则设置游标 -1 从上个块重新扫描
        if(verifyIsolatedBlock(needScanBlock)){
            recursionProcessErrorBlock(needScanBlock-1);
            return;
        }

        //将块信息和tx同步到数据库
        syncBlockAndTx(needScanBlock);

    }

    @Override
    public void updateAccount() {

    }

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
    public abstract boolean verifyIsolatedBlock(Long needScanBlock);



    /**
     * 将块信息和tx同步到数据库
     */
    public abstract void syncBlockAndTx(Long blockHeight);


}
