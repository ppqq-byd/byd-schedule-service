package com.ora.blockchain.service.rpc;

import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import com.ora.blockchain.service.web3j.Web3;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class Web3Test {

    @Test
    public void testGetTransactionList() {
        try {
            //String version = Web3.getWeb3ClientVersion();
           // System.out.println(version);
            System.out.println(Web3.getInstance(CoinType.ETH.name()).getCurrentBlockHeight());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
