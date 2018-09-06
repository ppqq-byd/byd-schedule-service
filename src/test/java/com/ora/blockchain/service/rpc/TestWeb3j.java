package com.ora.blockchain.service.rpc;

import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

public class TestWeb3j {
    @Test
    public void testGetTransactionList() {
        try {
        /*    Web3j web3j =
                    Web3j.build(
                            new HttpService("http://107.150.99.155:8545"));*/
            Web3j web3j =
                    Web3j.build(
                            new HttpService("http://52.83.130.131:8545"));
            System.out.println(web3j.ethBlockNumber().send().getBlockNumber());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
