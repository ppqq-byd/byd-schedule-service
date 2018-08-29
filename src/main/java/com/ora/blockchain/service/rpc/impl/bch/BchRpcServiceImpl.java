package com.ora.blockchain.service.rpc.impl.bch;

import com.ora.blockchain.service.rpc.impl.RpcServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service("bchRpcServiceImpl")
public class BchRpcServiceImpl extends RpcServiceImpl {

    @Resource
    @Qualifier("bchRpcRestTemplate")
    private RestTemplate rpcRestTemplate;

    @Override
    public RestTemplate getRpcRestTemplate() {
        return rpcRestTemplate;
    }
}
