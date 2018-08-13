package com.ora.blockchain.service.transaction;

import com.ora.blockchain.service.blockscanner.IBlockScanner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.Date;


@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class IEthereumTransactionTest {

    @Autowired
    private IBlockScanner ethService;

    @Test
    public void testInsertNewBlock() throws Exception {
        Long start = System.currentTimeMillis();
        ethService.scanBlock(6004178L);
        Long end = System.currentTimeMillis();
        System.out.println("cost--------------"+(end - start));
    }

    @Test
    public void testVerifyBlock(){
        Long start = System.currentTimeMillis();

        Long end = System.currentTimeMillis();
        System.out.println("cost--------------"+(end - start));
    }

}
