package com.ora.blockchain.service.block.impl.ltc;

import com.ora.blockchain.service.block.impl.BlockServiceImpl;
import com.ora.blockchain.service.rpc.IRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

//@Resource(name = "ltcBlockServiceImpl")
@Service("ltcBlockServiceImpl")
public class LtcBlockServiceImpl extends BlockServiceImpl {
    @Resource
    @Qualifier("ltcRpcServiceImpl")
    private IRpcService ltcRpcService;

    public IRpcService getRpcService() {
        return ltcRpcService;
    }
}
