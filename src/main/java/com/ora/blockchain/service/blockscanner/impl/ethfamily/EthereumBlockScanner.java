package com.ora.blockchain.service.blockscanner.impl.ethfamily;


import com.ora.blockchain.mybatis.mapper.block.EthereumBlockMapper;
import com.ora.blockchain.mybatis.mapper.transaction.EthereumTransactionMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBindMapper;
import com.ora.blockchain.service.blockscanner.impl.BlockScanner;
import com.ora.blockchain.service.web3j.Web3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("ethBlockScaner")
public class EthereumBlockScanner extends BlockScanner {

    @Autowired
    private EthereumTransactionMapper txMapper;

    @Autowired
    private EthereumBlockMapper blockMapper;

    @Autowired
    private WalletAccountBindMapper accountBindMapper;

    private static final int DEPTH = 12;

    @Override
    public boolean isNeedScanHeightLasted(Long needScanBlock) {
        return false;
    }

    @Override
    public void deleteBlockAndUpdateTx(Long initBlockHeight) {

    }

    @Override
    public Long getNeedScanBlockHeight(Long initBlockHeight){
        long dbBlockHeight = blockMapper.queryMaxBlockInDb("coin_eth");
        if(dbBlockHeight==0){
            return initBlockHeight;
        }

        return dbBlockHeight++;
    }

    @Override
    public boolean verifyIsolatedBlock(Long needScanBlock) {

        return false;
    }

    @Override
    public void syncBlockAndTx(Long blockHeight) {

    }
}
