package com.ora.blockchain.service.transaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class IEthereumTransactionTest {

    @Autowired
    private IEthereumTransactionService ethService;

    @Test
    public void testInsertNewBlock(){
        ethService.inserNewBlock();
    }

    @Test
    public void testVerifyBlock(){
        ethService.confirmBlock();

    }
}
