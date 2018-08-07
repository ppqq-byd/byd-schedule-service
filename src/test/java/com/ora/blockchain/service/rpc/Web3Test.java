package com.ora.blockchain.service.rpc;

import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import com.ora.blockchain.service.web3j.Web3;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class Web3Test {

    @Test
    public void testGetTransactionList() {
        try {
            System.out.println(Web3.getCurrentBlockHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
