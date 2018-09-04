package com.ora.blockchain.service.block;

import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.service.blockscanner.IBlockScanner;
import com.ora.blockchain.service.rpc.IRpcService;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Qualifier("darkBlockServiceImpl")
    private IBlockService darkBlockService;
    @Resource
    @Qualifier("darkRpcServiceImpl")
    private IRpcService darkRpcService;

    @Resource
    @Qualifier("ltcBlockServiceImpl")
    private IBlockService ltcBlockService;
    @Resource
    @Qualifier("ltcRpcServiceImpl")
    private IRpcService ltcRpcService;

    @Resource
    @Qualifier("btcBlockServiceImpl")
    private IBlockService btcBlockService;
    @Resource
    @Qualifier("btcRpcServiceImpl")
    private IRpcService btcRpcService;

    @Resource
    @Qualifier("btcBlockScanner")
    private IBlockScanner btcScanner;

    @Resource
    @Qualifier("darkBlockScanner")
    private IBlockScanner darkScanner;

    @Resource
    @Qualifier("dogeBlockScanner")
    private IBlockScanner dogeScanner;

    @Resource
    @Qualifier("bchBlockScanner")
    private IBlockScanner bchScanner;

    @Resource
    @Qualifier("btgBlockScanner")
    private IBlockScanner btgScanner;

    @Test
    public void testScanner() throws Exception{
        long start = System.currentTimeMillis();
        for(int i=0;i<1;i++){
            btcScanner.scanBlock(539022L,CoinType.BTC.name());
            btcScanner.updateAccount(CoinType.BTC.name());
        }
        long end = System.currentTimeMillis();
        System.out.println("---------------------------"+(end-start)+"----------------------------------------------");
    }
}
