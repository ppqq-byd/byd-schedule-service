package com.ora.blockchain.service.block.impl.doge;

import com.ora.blockchain.service.block.impl.BlockServiceImpl;
import com.ora.blockchain.service.rpc.IRpcService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("dogeBlockServiceImpl")
public class DogeBlockServiceImpl extends BlockServiceImpl {

    @Resource
    @Qualifier("dogeRpcServiceImpl")
    private IRpcService rpcService;

    public IRpcService getRpcService() {
        return rpcService;
    }
}
