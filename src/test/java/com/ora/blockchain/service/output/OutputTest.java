package com.ora.blockchain.service.output;

import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.mapper.output.OutputMapper;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OutputTest {

    @Autowired
    private OutputMapper outputMapper;

    @Test
    public void testUpdateOutputBatch(){
        List<Output> outputList = new ArrayList<>();
        Output o1 = new Output();
        o1.setN(0);
        o1.setTransactionTxid("aa80e2a0270d2d36cb7323e026aaf41f8039901302a26f1445cee6a869449e2a");
        outputList.add(o1);
        Output o2 = new Output();
        o2.setN(1);
        o2.setTransactionTxid("aa80e2a0270d2d36cb7323e026aaf41f8039901302a26f1445cee6a869449e2a");
        outputList.add(o2);
        outputMapper.updateOutputBatch("coin_btc",0,outputList);
    }

}
