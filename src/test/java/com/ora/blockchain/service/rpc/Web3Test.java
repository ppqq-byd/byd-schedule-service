package com.ora.blockchain.service.rpc;

import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import com.ora.blockchain.service.web3j.Web3;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class Web3Test {

    @Resource
    @Qualifier("ethWeb3j")
    private Web3 ethWeb3;

    @Test
    public void testGetTransactionList() {
        try {
            //String version = Web3.getWeb3ClientVersion();
           // System.out.println(version);
            System.out.println(ethWeb3.getCurrentBlockHeight());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
