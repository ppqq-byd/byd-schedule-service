package com.ora.blockchain.service.blockscanner.impl.btcfamily.bch;

import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.blockscanner.impl.btcfamily.BtcfamilyBlockScanner;
import com.ora.blockchain.service.rpc.IRpcService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("bchBlockScanner")
public class BchBlockScanner extends BtcfamilyBlockScanner {
    @Resource
    @Qualifier("bchRpcServiceImpl")
    private IRpcService rpcService;
    @Resource
    @Qualifier("bchBlockServiceImpl")
    private IBlockService blockService;

    @Override
    public IBlockService getBlockService() {
        return blockService;
    }

    @Override
    public IRpcService getRpcService() {
        return rpcService;
    }

    @Override
    public String getCoinType() {

        return CoinType.BCH.name();
    }
}
