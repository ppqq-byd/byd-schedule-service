package com.ora.blockchain.service.rpc.impl.dark;

import com.fasterxml.jackson.databind.JsonNode;
import com.ora.blockchain.service.rpc.IRpcService;
import com.ora.blockchain.service.rpc.impl.RpcServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Arrays;

@Slf4j
@Service("darkRpcServiceImpl")
public class DarkRpcServiceImpl extends RpcServiceImpl {

    @Resource
    @Qualifier("darkRpcRestTemplate")
    private RestTemplate darkRpcRestTemplate;

    @Override
    public RestTemplate getRpcRestTemplate() {
        return darkRpcRestTemplate;
    }

    @Override
    public JsonNode getTransaction(String blockHash, String transactionId) {
        return rpcRequest("getrawtransaction",transactionId,1);
    }
}
