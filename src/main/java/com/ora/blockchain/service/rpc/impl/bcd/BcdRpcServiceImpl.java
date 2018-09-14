package com.ora.blockchain.service.rpc.impl.bcd;

import com.fasterxml.jackson.databind.JsonNode;
import com.ora.blockchain.service.rpc.impl.RpcServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Slf4j
@Service("bcdRpcServiceImpl")
public class BcdRpcServiceImpl extends RpcServiceImpl {
    @Resource
    @Qualifier("bcdRpcRestTemplate")
    private RestTemplate bcdRpcRestTemplate;

    @Override
    public RestTemplate getRpcRestTemplate() {
        return bcdRpcRestTemplate;
    }

    @Override
    public JsonNode getTransaction(String blockHash, String transactionId) {
        return rpcRequest("getrawtransaction",transactionId,1);
    }
}
