package com.ora.blockchain.service.blockscanner.impl.btcfamily.btc;

import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.blockscanner.impl.btcfamily.BtcfamilyBlockScanner;
import com.ora.blockchain.service.rpc.IRpcService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("btcBlockScanner")
public class BtcBlockScanner extends BtcfamilyBlockScanner {
    @Resource
    @Qualifier("btcRpcServiceImpl")
    private IRpcService rpcService;
    @Resource
    @Qualifier("btcBlockServiceImpl")
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
        return Constants.COIN_TYPE_BTC;
    }
}
