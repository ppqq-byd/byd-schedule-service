package com.ora.blockchain.service.block.impl.btc;

import com.ora.blockchain.service.block.impl.BlockServiceImpl;
import com.ora.blockchain.service.rpc.IRpcService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("btcBlockServiceImpl")
public class BtcBlockServiceImpl extends BlockServiceImpl {

    @Resource
    @Qualifier("btcRpcServiceImpl")
    private IRpcService btcRpcService;

    public IRpcService getRpcService() {
        return btcRpcService;
    }
}
