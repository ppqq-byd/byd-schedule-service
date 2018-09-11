package com.ora.blockchain.service.rpc.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import com.ora.blockchain.service.rpc.IRpcService;
import com.ora.blockchain.utils.ThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
public abstract class RpcServiceImpl implements IRpcService {

    @Override
    public List<Transaction> getTransactionList(String lastBlockhash){
        long start = System.currentTimeMillis();
        List<JsonNode> nodeList = getTransactions(lastBlockhash);
        long end = System.currentTimeMillis();
        log.info("getTransactions spent : " + (end - start));
        return IRpcService.convertToTransactionList(nodeList);
    }

    private List<JsonNode> getTransactions(String blockHash) {
        Block block = getBlock(blockHash);
        if (null != block) {
            List<Callable<JsonNode>> callables = new ArrayList<>();
            for (String txid : block.getTxidList()) {
                callables.add(() -> getTransaction(blockHash, txid));
            }
            return ThreadPool.addTask(callables);
        }
        return null;
    }

    protected JsonNode rpcRequest(String method,Object ... obj){
        HttpEntity<String> transactionRequestEntity = IRpcService.getRequestEntity(method, null == obj ?new ArrayList() : Arrays.asList(obj));
        try {
            JsonNode transaction = getRpcRestTemplate().exchange("/", HttpMethod.POST, transactionRequestEntity, JsonNode.class).getBody().get("result");
            return transaction;
        } catch (HttpServerErrorException e) {
            String responseStr = e.getResponseBodyAsString();
            String message = method + " failed:" + responseStr;
            log.error(message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to extract transaction data", e);
        }
        return null;
    }

    public Long getBestBlockHeight(){
        String blockHash = getBestBlockHash();
        if(StringUtils.isBlank(blockHash))
            return null;
        Block res = getBlock(blockHash);
        return null != res ? res.getHeight() : null;
    }

    @Override
    public String getBestBlockHash(){
        JsonNode res = rpcRequest("getbestblockhash");
        return  null == res ? null : res.asText();
    }

    @Override
    public String getBlockHash(Long blockHeight){
        if(null == blockHeight)
            return null;

        JsonNode res = rpcRequest("getblockhash",blockHeight);
        return null == res ? null : res.asText();
    }

    @Override
    public Block getBlock(String blockHash){
        return IRpcService.convertToBlock(rpcRequest("getblock",blockHash));
    }

    @Override
    public Block getBlock(Long blockHeight){
        String blockHash = getBlockHash(blockHeight);
        if(StringUtils.isBlank(blockHash))
            return null;
        return getBlock(blockHash);
    }

    protected JsonNode getTransaction(String blockHash,String transactionId) {
        return rpcRequest("getrawtransaction",transactionId,1,blockHash);
    }

    public abstract RestTemplate getRpcRestTemplate();
}
