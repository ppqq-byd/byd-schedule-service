package com.ora.blockchain.service.rpc.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import com.ora.blockchain.service.rpc.IRpcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public abstract class RpcServiceImpl implements IRpcService {

//    @Autowired
//    private RestTemplate darkRpcRestTemplate;

    @Override
    public List<Transaction> getTransactionList(Integer blockDepth, String lastBlockhash){
        List<JsonNode> nodeList = getTransactions(blockDepth,lastBlockhash);
        return IRpcService.convertToTransactionList(nodeList);
    }

    private List<JsonNode> getTransactions(Integer blockDepth, String lastBlockhash) {
        String blockHash = null;
        if (lastBlockhash == null) {
            HttpEntity<String> blockHashRequestEntity = IRpcService.getRequestEntity("getbestblockhash", new ArrayList<>());
            blockHash = getRpcRestTemplate().exchange("/", HttpMethod.POST, blockHashRequestEntity,
                    new ParameterizedTypeReference<RpcResponse<String>>() {
                    }).getBody().getResult();
        } else {
            blockHash = lastBlockhash;
        }
        ;
        log.info("Scanning depth = " + blockDepth + ", blockHash = " + blockHash);
        HttpEntity<String> blockRequestEntity = IRpcService.getRequestEntity("getblock", Arrays.asList(blockHash));
        BlockInfo blockInfo = getRpcRestTemplate().exchange("/", HttpMethod.POST, blockRequestEntity,
                new ParameterizedTypeReference<RpcResponse<BlockInfo>>() {
                }).getBody().getResult();

        log.info("Processing transactions in block (n = " + blockInfo.getTx().size() + ")");
        List<JsonNode> transactions = new ArrayList<>();
        for (String transactionId : blockInfo.getTx()) {
            log.info("getting raw transaction for transactionId = " + transactionId);
            HttpEntity<String> transactionRequestEntity = IRpcService.getRequestEntity("getrawtransaction", Arrays.asList(transactionId, 1));
            try {
                JsonNode transaction = getRpcRestTemplate()
                        .exchange("/", HttpMethod.POST, transactionRequestEntity, JsonNode.class).getBody()
                        .get("result");
                log.info(transactionId + " : " + transaction.toString());
                transactions.add(transaction);
            } catch (HttpServerErrorException e) {
                log.warn("Failed to get transaction data" + e.getResponseBodyAsString());
            } catch (Exception e) {
                log.warn("Failed to extract transaction data", e);
            }
        }
        // scan blocks recursively until blockDepth is reached
        if (blockDepth.intValue() > 1) {
            String previousBlockHash = blockInfo.getPreviousblockhash();
            transactions.addAll(getTransactions(blockDepth - 1, previousBlockHash));
        }
        return transactions;
    }

    @Override
    public List<Block> getNextBlockList(Integer blockDepth,String lastBlockhash){
        List<JsonNode> nodeList = getBlocks("nextblockhash",blockDepth,lastBlockhash);
        return IRpcService.convertToBlockList(nodeList);
    }

    @Override
    public List<Block> getPreviousBlockList(Integer blockDepth,String lastBlockhash){
        List<JsonNode> nodeList = getBlocks("previousblockhash",blockDepth,lastBlockhash);
        return IRpcService.convertToBlockList(nodeList);
    }

    private List<JsonNode> getBlocks(String direction, Integer blockDepth, String lastBlockhash) {
        String blockHash = null;
        if (lastBlockhash == null) {
            HttpEntity<String> blockHashRequestEntity = IRpcService.getRequestEntity("getbestblockhash", new ArrayList<>());
            blockHash = getRpcRestTemplate().exchange("/", HttpMethod.POST, blockHashRequestEntity,
                    new ParameterizedTypeReference<RpcResponse<String>>() {
                    }).getBody().getResult();
        } else {
            blockHash = lastBlockhash;
        }
        log.info("Scanning depth = " + blockDepth + ", blockHash = " + blockHash);
        HttpEntity<String> blockRequestEntity = IRpcService.getRequestEntity("getblock", Arrays.asList(blockHash));

        List<JsonNode> blocks = new ArrayList<>();
        JsonNode blockNode = getRpcRestTemplate().exchange("/",HttpMethod.POST,blockRequestEntity,JsonNode.class).getBody().get("result");
        log.info(blockNode.toString());
        // scan blocks recursively until blockDepth is reached
        blocks.add(blockNode);
        if (blockDepth.intValue() > 1) {
            String previousBlockHash = blockNode.get(direction).textValue();
            blocks.addAll(getBlocks(direction,blockDepth - 1, previousBlockHash));
        }
        return blocks;
    }

    public abstract RestTemplate getRpcRestTemplate();
}
