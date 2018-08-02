package com.ora.blockchain.service.block.impl.dark;

import com.ora.blockchain.service.block.impl.BlockServiceImpl;
import com.ora.blockchain.service.rpc.IRpcService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("darkBlockServiceImpl")
public class DarkBlockServiceImpl extends BlockServiceImpl {

    @Resource
    @Qualifier("darkRpcServiceImpl")
    private IRpcService darkRpcService;

    public IRpcService getRpcService() {
        return darkRpcService;
    }
}
