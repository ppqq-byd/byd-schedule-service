package com.ora.blockchain.service.block.impl.btg;

import com.ora.blockchain.service.block.impl.BlockServiceImpl;
import com.ora.blockchain.service.rpc.IRpcService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("btgBlockServiceImpl")
public class BtgBlockServiceImpl extends BlockServiceImpl {

    @Resource
    @Qualifier("btgRpcServiceImpl")
    private IRpcService rpcService;

    public IRpcService getRpcService() {
        return rpcService;
    }
}
