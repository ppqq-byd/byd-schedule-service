package com.ora.blockchain.service.block.impl.bcd;

import com.ora.blockchain.service.block.impl.BlockServiceImpl;
import com.ora.blockchain.service.rpc.IRpcService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("bcdBlockServiceImpl")
public class BcdBlockServiceImpl extends BlockServiceImpl {
    @Resource
    @Qualifier("bcdRpcServiceImpl")
    private IRpcService bcdRpcService;

    public IRpcService getRpcService() {
        return bcdRpcService;
    }
}
