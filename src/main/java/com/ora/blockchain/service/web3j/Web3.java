package com.ora.blockchain.service.web3j;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import rx.Subscription;

import java.io.IOException;
import java.math.BigInteger;

public class Web3 {

    static Web3j web3 = Web3j.build(new HttpService(
           "http://52.83.130.131:8545"));


    /**
     * 根据账户地址获取Nonce
     * @param address
     * @return
     * @throws Exception
     */
    public static BigInteger getNonce(String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount =
                web3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();
        return ethGetTransactionCount.getTransactionCount();
    }

    /**
     * 获取当前区块高度
     * @return
     * @throws IOException
     */
    public static BigInteger getCurrentBlockHeight() throws IOException {
       return web3.ethBlockNumber().send().getBlockNumber();
    }

    /**
     * 根据账户地址获取余额
     * @param address
     * @return
     * @throws Exception
     */
    public static BigInteger getBalance(String address) throws Exception {
        EthGetBalance ethGetBalance1 = web3.ethGetBalance(address,
                DefaultBlockParameter.valueOf("latest")).send();
        return ethGetBalance1.getBalance();
    }

    public static String getWeb3ClientVersion() throws Exception {
        Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().send();
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();

        Web3j web3 = Web3j.build(new HttpService());  // defaults to http://localhost:8545/
        web3.web3ClientVersion().observable().subscribe(x -> {
            String aaa = x.getWeb3ClientVersion();
            System.out.println("aaa:"+aaa);
        });
        return clientVersion;
    }



    public static void getPending(){
        Subscription subscription = web3.pendingTransactionObservable().subscribe(tx -> {

        });
    }

    /**
     * 根据区块数获取区块信息
     * @param number
     * @return
     * @throws Exception
     */
    public static EthBlock getBlockInfoByNumber(Long number) throws Exception {
        EthBlock block = web3.ethGetBlockByNumber(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(number)), true).send();

        return block;
    }

    /**
     * 根据区块hash 获取区块信息
     * @param hash
     * @return
     * @throws Exception
     */
    public static EthBlock getBlockInfoByHash(String hash) throws Exception {
        boolean returnFullTransactionObjects = true;
        EthBlock block = web3.ethGetBlockByHash(hash,returnFullTransactionObjects).send();

        return block;
    }
}
