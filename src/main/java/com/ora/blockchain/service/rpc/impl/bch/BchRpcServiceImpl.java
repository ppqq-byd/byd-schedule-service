package com.ora.blockchain.service.rpc.impl.bch;

import com.fasterxml.jackson.databind.JsonNode;
import com.ora.blockchain.service.rpc.impl.RpcServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Slf4j
@Service("bchRpcServiceImpl")
public class BchRpcServiceImpl extends RpcServiceImpl {

    @Resource
    @Qualifier("bchRpcRestTemplate")
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
