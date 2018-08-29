package com.ora.blockchain.service.block.impl.bch;

import com.ora.blockchain.service.block.impl.BlockServiceImpl;
import com.ora.blockchain.service.rpc.IRpcService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("bchBlockServiceImpl")
public class BchBlockServiceImpl extends BlockServiceImpl {

    @Resource
    @Qualifier("bchRpcServiceImpl")
    private IRpcService rpcService;

    public IRpcService getRpcService() {
        return rpcService;
    }
}
