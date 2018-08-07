package com.ora.blockchain.service.rpc.impl.btc;

import com.ora.blockchain.service.rpc.impl.RpcServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service("btcRpcServiceImpl")
public class BtcRpcServiceImpl extends RpcServiceImpl {

    @Resource
    @Qualifier("btcRpcRestTemplate")
    private RestTemplate btcRpcRestTemplate;

    @Override
    public RestTemplate getRpcRestTemplate() {
        return btcRpcRestTemplate;
    }
}
