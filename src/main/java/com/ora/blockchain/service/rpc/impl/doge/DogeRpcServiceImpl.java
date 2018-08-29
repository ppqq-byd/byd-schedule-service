package com.ora.blockchain.service.rpc.impl.doge;

import com.fasterxml.jackson.databind.JsonNode;
import com.ora.blockchain.service.rpc.impl.RpcServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service("dogeRpcServiceImpl")
public class DogeRpcServiceImpl extends RpcServiceImpl {

    @Resource
    @Qualifier("dogeRpcRestTemplate")
    private RestTemplate rpcRestTemplate;

    @Override
    public RestTemplate getRpcRestTemplate() {
        return rpcRestTemplate;
    }

    @Override
    public JsonNode getTransaction(String blockHash, String transactionId) {
        return rpcRequest("getrawtransaction",transactionId,1);
    }
}
