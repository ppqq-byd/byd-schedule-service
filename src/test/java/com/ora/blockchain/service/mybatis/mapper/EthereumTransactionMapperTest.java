package com.ora.blockchain.service.mybatis.mapper;

import com.ora.blockchain.mybatis.entity.transaction.EthereumTransaction;
import com.ora.blockchain.mybatis.mapper.transaction.EthereumTransactionMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class EthereumTransactionMapperTest {

    @Autowired
    private EthereumTransactionMapper mapper;

    @Test
    public void testInsert(){
        EthereumTransaction tx = new EthereumTransaction();
        tx.setTxId("aaa");
        tx.setFrom("a");
        tx.setTo("b");
        mapper.insertTransaction("coin_eth",tx);
    }
}
