package com.ora.blockchain.service.blockscanner.impl.btcfamily.btg;

import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.blockscanner.impl.btcfamily.BtcfamilyBlockScanner;
import com.ora.blockchain.service.rpc.IRpcService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("btgBlockScanner")
public class BtgBlockScanner extends BtcfamilyBlockScanner {
    @Resource
    @Qualifier("btgRpcServiceImpl")
    private IRpcService rpcService;
    @Resource
    @Qualifier("btgBlockServiceImpl")
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

        return CoinType.BTG.name();
    }
}
