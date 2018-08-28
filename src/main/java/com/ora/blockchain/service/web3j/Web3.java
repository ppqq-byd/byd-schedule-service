package com.ora.blockchain.service.web3j;

import com.ora.blockchain.constants.CoinType;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import rx.Subscription;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Web3 {

    private static Web3 web3Instance = null;

    private Web3j web3;

    //http://52.83.130.131:8545
    public static synchronized Web3 getInstance(String coinType){
        if(web3Instance==null){
            web3Instance = new Web3(coinType);
        }

        return web3Instance;
    }

    private Web3(String coinType){
        if(CoinType.ETC.name().equals(coinType)){
            web3 = Web3j.build(new HttpService("http://52.83.130.131:8545"));
        }else  if(CoinType.ETH.name().equals(coinType)){
            web3 = Web3j.build(new HttpService("http://52.83.130.131:8545"));
        }


    }


    /**
     * 根据账户地址获取Nonce
     * @param address
     * @return
     * @throws Exception
     */
    public  BigInteger getNonce(String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount =
                web3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();
        return ethGetTransactionCount.getTransactionCount();
    }

    /**
     * 获取当前区块高度
     * @return
     * @throws IOException
     */
    public  BigInteger getCurrentBlockHeight() throws IOException {
       return web3.ethBlockNumber().send().getBlockNumber();
    }

    /**
     * 根据账户地址获取余额
     * @param address
     * @return
     * @throws Exception
     */
    public  BigInteger getBalance(String address) throws Exception {
        EthGetBalance ethGetBalance1 = web3.ethGetBalance(address,
                DefaultBlockParameter.valueOf("latest")).send();
        return ethGetBalance1.getBalance();
    }

    public  String getWeb3ClientVersion() throws Exception {
        Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().send();
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();
        System.out.println(clientVersion);
        return clientVersion;
    }



    public  void getPending(){
        Subscription subscription = web3.pendingTransactionObservable().subscribe(tx -> {

        });
    }

    /**
     * 根据区块数获取区块信息
     * @param number
     * @return
     * @throws Exception
     */
    public  EthBlock getBlockInfoByNumber(Long number) throws Exception {
        EthBlock block = web3.ethGetBlockByNumber(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(number)), true).send();

        return block;
    }

    /**
     * 根据txhash 获取 TransactionReceipt
     * @param txHash
     * @return
     * @throws Exception
     */
    public  TransactionReceipt getTransactionReceiptByTxhash(String txHash) throws Exception {
        TransactionReceipt tr = web3.ethGetTransactionReceipt(txHash).send().getResult();

        return tr;

    }

    /**
     * 根据区块hash 获取区块信息
     * @param hash
     * @return
     * @throws Exception
     */
    public  EthBlock getBlockInfoByHash(String hash) throws Exception {
        boolean returnFullTransactionObjects = true;
        EthBlock block = web3.ethGetBlockByHash(hash,returnFullTransactionObjects).send();

        return block;
    }
}
