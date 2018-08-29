package com.ora.blockchain.service.rpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.exception.BlockchainException;
import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.mybatis.entity.input.Input;
import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import com.ora.blockchain.service.rpc.impl.RpcRequest;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface IRpcService {
    public static List<Transaction> convertToTransactionList(List<JsonNode> nodeList) {
        if (null == nodeList || nodeList.isEmpty()) {
            return null;
        }
        return nodeList.stream().map(node -> {
            if (null == node) {
                return null;
            }
            Transaction transaction = new Transaction();
            boolean coinbaseTransaction = false;
            if (node.has("txid"))
                transaction.setTxid(node.get("txid").textValue());
            if (node.has("hex"))
                transaction.setHex(node.get("hex").textValue());
            if (node.has("size"))
                transaction.setSize(node.get("size").longValue());
            if (node.has("version"))
                transaction.setVersion(node.get("version").longValue());
            if (node.has("locktime"))
                transaction.setLocktime(node.get("locktime").longValue());
            if (node.has("blockhash"))
                transaction.setBlockHash(node.get("blockhash").textValue());
            if (node.has("height"))
                transaction.setHeight(node.get("height").longValue());
            if (node.has("time"))
                transaction.setTime(node.get("time").longValue());
            if (node.has("blocktime"))
                transaction.setBlockTime(node.get("blocktime").longValue());
            if (node.has("vin")) {
                List<Input> inputList = new ArrayList<>();
                ArrayNode vins = (ArrayNode) node.get("vin");
                for (JsonNode vin : vins) {
                    Input input = new Input();
                    if (node.has("blockhash"))
                        input.setBlockHash(node.get("blockhash").textValue());
                    if (node.has("txid"))
                        input.setTransactionTxid(node.get("txid").textValue());
                    if (vin.has("sequence"))
                        input.setSequence(vin.get("sequence").longValue());
                    if (vin.has("scriptSig")) {
                        JsonNode scriptSig = vin.get("scriptSig");
                        if (scriptSig.has("hex"))
                            input.setScriptSigHex(scriptSig.get("hex").textValue());
                        if (scriptSig.has("asm"))
                            input.setScriptSigAsm(scriptSig.get("asm").textValue());
                        if (vin.has("txid"))
                            input.setTxid(vin.get("txid").textValue());
                        if (vin.has("vout"))
                            input.setVout(vin.get("vout").intValue());
                    } else if (vin.has("coinbase")) {
                        input.setCoinbase(vin.get("coinbase").textValue());
                        coinbaseTransaction = true;
                    }
                    inputList.add(input);
                }
                transaction.setInputList(inputList);
            }

            if (node.has("vout")) {
                List<Output> outputList = new ArrayList<>();
                ArrayNode vouts = (ArrayNode) node.get("vout");
                for (JsonNode vout : vouts) {
                    Output output = new Output();
                    output.setCoinbase(coinbaseTransaction ? 1 : 0);
                    if (node.has("blockhash"))
                        output.setBlockHash(node.get("blockhash").textValue());
                    if (node.has("txid"))
                        output.setTransactionTxid(node.get("txid").textValue());
                    if (vout.has("value"))
                        output.setValue(vout.get("value").doubleValue());
                    if (vout.has("valueSat"))
                        output.setValueSat(new BigInteger(vout.get("valueSat").textValue()));
                    if (vout.has("n"))
                        output.setN(vout.get("n").intValue());
                    if (vout.has("scriptPubKey")) {
                        JsonNode scriptPubKey = vout.get("scriptPubKey");
                        if (scriptPubKey.has("asm"))
                            output.setScriptPubKeyAsm(scriptPubKey.get("asm").textValue());
                        if (scriptPubKey.has("hex"))
                            output.setScriptPubKeyHex(scriptPubKey.get("hex").textValue());
                        if (scriptPubKey.has("reqSigs"))
                            output.setScriptPubKeyReqSigs(scriptPubKey.get("reqSigs").intValue());
                        if (scriptPubKey.has("type"))
                            output.setScriptPubKeyType(scriptPubKey.get("type").textValue());
                        if (scriptPubKey.has("addresses")) {
                            ArrayNode recipientAddressNodes = (ArrayNode) scriptPubKey.get("addresses");
                            String addresses = "";
                            for (JsonNode addr : recipientAddressNodes) {
                                addresses = addresses + addr.textValue() + ",";
                            }
                            output.setScriptPubKeyAddresses(addresses.substring(0, addresses.length() - 1));
                        }
                    }
                    outputList.add(output);
                }
                transaction.setOutputList(outputList);
            }
            return transaction;
        }).collect(Collectors.toList());
    }

    public static Block convertToBlock(JsonNode node) {
        if (null == node) {
            return null;
        }
        Block block = new Block();
        if(node.has("hash"))
            block.setBlockHash(node.get("hash").textValue());
        if(node.has("size"))
            block.setSize(node.get("size").longValue());
        if(node.has("height"))
            block.setHeight(node.get("height").longValue());
        if(node.has("version"))
            block.setVersion(node.get("version").longValue());
        if(node.has("merkleroot"))
            block.setMerkleroot(node.get("merkleroot").textValue());
        if(node.has("time"))
            block.setTime(node.get("time").longValue());
        if(node.has("mediantime"))
            block.setMedianTime(node.get("mediantime").longValue());
        if(node.has("nonce"))
            block.setNonce(node.get("nonce").longValue());
        if(node.has("bits"))
            block.setBits(node.get("bits").textValue());
        if(node.has("difficulty"))
            block.setDifficulty(node.get("difficulty").doubleValue() + "");
        if(node.has("chainwork"))
            block.setChainwork(node.get("chainwork").textValue());
        if(node.has("previousblockhash"))
            block.setPreviousBlockHash(node.get("previousblockhash").textValue());
        if (node.has("nextblockhash"))
            block.setNextBlockHash(node.get("nextblockhash").textValue());
        if (node.has("tx")){
            List<String> txidList = new ArrayList<>();
            ArrayNode txids = (ArrayNode) node.get("tx");
            for(JsonNode txNode : txids){
                txidList.add(txNode.asText());
            }
            block.setTxidList(txidList);
        }
        return block;
    }

    public static HttpEntity<String> getRequestEntity(String method, List<Object> params) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "text/plain");

        RpcRequest darkRpcRequest = create(method, params);

        String body;
        try {
            body = new ObjectMapper().writeValueAsString(darkRpcRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new BlockchainException(Constants.ORA_RPC_EXCEPTION, Constants.ORA_RPCEXCEPTION_MSG);
        }
        return new HttpEntity<>(body, headers);
    }

    public static RpcRequest create(String method, List<Object> params) {
        RpcRequest request = new RpcRequest();
        request.setMethod(method);
        request.setParams(params);
        return request;
    }

    public Long getBestBlockHeight();

    public String getBlockHash(Long blockHeight);

    public String getBestBlockHash();

    public Block getBlock(String blockHash);

    public Block getBlock(Long blockHeight);

    public List<Transaction> getTransactionList(String blockhash);
}
