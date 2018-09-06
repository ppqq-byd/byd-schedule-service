package com.ora.blockchain.service.web3j;

import com.ora.blockchain.config.EthereumWeb3jSettings;
import com.ora.blockchain.constants.CoinType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

@Service
@Slf4j
public abstract class Web3 {

    @Autowired
    private EthereumWeb3jSettings settings;

    //"http://52.83.130.131:8545"
    public abstract Web3j getWeb3jClient();




    /**
     * 根据账户地址获取Nonce
     * @param address
     * @return
     * @throws Exception
     */
    public  BigInteger getNonce(String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount =
                getWeb3jClient().ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();
        return ethGetTransactionCount.getTransactionCount();
    }

    /**
     * 获取当前区块高度
     * @return
     * @throws IOException
     */
    public  BigInteger getCurrentBlockHeight() throws IOException {
       return getWeb3jClient().ethBlockNumber().send().getBlockNumber();
    }

    /**
     * 根据账户地址获取余额
     * @param address
     * @return
     * @throws Exception
     */
    public  BigInteger getBalance(String address) throws Exception {
        EthGetBalance ethGetBalance1 = getWeb3jClient().ethGetBalance(address,
                DefaultBlockParameter.valueOf("latest")).send();
        return ethGetBalance1.getBalance();
    }

    public  String getWeb3ClientVersion() throws Exception {
        Web3ClientVersion web3ClientVersion = getWeb3jClient().web3ClientVersion().send();
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();
        System.out.println(clientVersion);
        return clientVersion;
    }



    public  void getPending(){
        Subscription subscription = getWeb3jClient().pendingTransactionObservable().subscribe(tx -> {

        });
    }

    /**
     * 根据区块数获取区块信息
     * @param number
     * @return
     * @throws Exception
     */
    public  EthBlock getBlockInfoByNumber(Long number) throws Exception {
        EthBlock block = getWeb3jClient().ethGetBlockByNumber(
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
        TransactionReceipt tr = getWeb3jClient().ethGetTransactionReceipt(txHash).send().getResult();

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
        EthBlock block = getWeb3jClient().ethGetBlockByHash(hash,returnFullTransactionObjects).send();

        return block;
    }
}
