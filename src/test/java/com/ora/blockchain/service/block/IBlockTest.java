package com.ora.blockchain.service.block;

import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.service.blockscanner.IBlockScanner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IBlockTest {
    @Resource
    @Qualifier("btcBlockScanner")
    private IBlockScanner btcScanner;

    @Resource
    @Qualifier("darkBlockScanner")
    private IBlockScanner darkScanner;

    @Resource
    @Qualifier("bcdBlockScanner")
    private IBlockScanner bcdScanner;

    @Qualifier("dogeBlockScanner")
    private IBlockScanner dogeScanner;

    @Resource
    @Qualifier("bchBlockScanner")
    private IBlockScanner bchScanner;

    @Resource
    @Qualifier("btgBlockScanner")
    private IBlockScanner btgScanner;

    @Resource
    @Qualifier("ltcBlockScanner")
    private IBlockScanner ltcScanner;

    @Test
    public void testDarkScanner(){
        for(int i=0;i<100;i++){
            try {
                darkScanner.scanBlock(921662L,CoinType.DARK.name());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testBcdScanner(){
        try {
            bcdScanner.scanBlock(531049L, CoinType.BCD.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testScanner() throws Exception{
        long start = System.currentTimeMillis();
        for(int i=0;i<100;i++){
            btcScanner.scanBlock(11386L,CoinType.BTC.name());
            btcScanner.updateAccount(CoinType.BTC.name());
//            btgScanner.scanBlock(545231L,CoinType.BTG.name());
//            btgScanner.updateAccount(CoinType.BTG.name());
//            dogeScanner.scanBlock(35550L,CoinType.DOGE.name());
//            dogeScanner.updateAccount(CoinType.DOGE.name());
//            darkScanner.scanBlock(932080L,CoinType.DARK.name());
//            darkScanner.updateAccount(CoinType.DARK.name());
//            ltcScanner.scanBlock(1486254L,CoinType.LTC.name());
//            ltcScanner.updateAccount(CoinType.LTC.name());

//            bchScanner.scanBlock(546414L,CoinType.BCH.name());
//            bchScanner.updateAccount(CoinType.BCH.name());
        }
        long end = System.currentTimeMillis();
        System.out.println("---------------------------"+(end-start)+"----------------------------------------------");
    }
}
