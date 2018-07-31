package com.ora.blockchain.service.rpc.impl.dark;

import com.ora.blockchain.service.rpc.impl.RpcServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DarkRpcServiceImpl extends RpcServiceImpl {

    @Autowired
    @Qualifier("darkRpcRestTemplate")
    private RestTemplate darkRpcRestTemplate;

    @Override
    public RestTemplate getRpcRestTemplate() {
        return darkRpcRestTemplate;
    }
}
