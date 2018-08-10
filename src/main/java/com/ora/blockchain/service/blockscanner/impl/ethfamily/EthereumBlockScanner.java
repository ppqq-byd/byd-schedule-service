package com.ora.blockchain.service.blockscanner.impl.ethfamily;


import com.ora.blockchain.service.blockscanner.impl.BlockScanner;
import org.springframework.stereotype.Service;

@Service("ethBlockServiceImpl")
public class EthereumBlockScanner extends BlockScanner {

    @Override
    public void deleteBlockAndUpdateTx(Long initBlockHeight) {
        
    }

    @Override
    public Long getNeedScanBlockHeight(Long initBlockHeight) {
        return null;
    }

    @Override
    public boolean verifyIsolatedBlock(Long needScanBlock) {
        return false;
    }

    @Override
    public void syncBlockAndTx(Long blockHeight) {

    }
}
