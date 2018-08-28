package com.ora.blockchain.service.transaction;

import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.service.blockscanner.IBlockScanner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class IEtcTxTest {

    @Resource
    @Qualifier("etcBlockScaner")
    private IBlockScanner etcService;

    @Test
    public void testInsertNewBlock() throws Exception {
        Long start = System.currentTimeMillis();
        etcService.scanBlock(802688L, CoinType.ETC.name());
        Long end = System.currentTimeMillis();
        System.out.println("cost--------------"+(end - start));
    }

}
