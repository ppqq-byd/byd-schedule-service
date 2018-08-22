package com.ora.blockchain.service.transaction;

import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.service.blockscanner.IBlockScanner;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;


@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class IEthereumTransactionTest {


    @Resource
    @Qualifier("ethBlockScaner")
    private IBlockScanner ethService;


    @Test
    public void testInsertNewBlock() throws Exception {
        Long start = System.currentTimeMillis();
        ethService.scanBlock(6169721L, CoinType.ETH.name());
        Long end = System.currentTimeMillis();
        System.out.println("cost--------------"+(end - start));
    }

    @Test
    public void testVerifyBlock(){
        Long start = System.currentTimeMillis();

        Long end = System.currentTimeMillis();
        System.out.println("cost--------------"+(end - start));
    }

    @Test
    public void testInput(){
        String input = "0xa9059cbb000000000000000000000000afa920790e1e9ef75c5f44fa847e8b0d04ee073f000000000000000000000000000000000000000000000000214e8348c4f00000";
        if(input.substring(0,10).equals("0xa9059cbb")){
            String toAddress = input.substring(10,74);
            String value = input.substring(74,138);
            System.out.println(toAddress);
            byte[] ss = new BigInteger(toAddress,16).toByteArray();
            toAddress = Hex.toHexString(ss);
            System.out.println("address:"+toAddress);

            Long v = new BigInteger(value,16).longValue();
            System.out.println("v:"+v);
        }
    }




}
