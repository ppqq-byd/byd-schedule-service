package com.ora.blockchain.service.rpc.impl.ltc;

import com.ora.blockchain.service.rpc.impl.RpcServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

//@Resource(name="ltcRpcServiceImpl")
@Service("ltcRpcServiceImpl")
public class LtcRpcServiceImpl extends RpcServiceImpl {

    @Resource
    @Qualifier("ltcRpcRestTemplate")
    private RestTemplate ltcRpcRestTemplate;

    @Override
    public RestTemplate getRpcRestTemplate(){
        return ltcRpcRestTemplate;
    }
}
