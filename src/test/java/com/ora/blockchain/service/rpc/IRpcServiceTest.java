package com.ora.blockchain.service.rpc;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.mybatis.entity.transaction.Transaction;
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
import java.util.List;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IRpcServiceTest {
    @Resource
    @Qualifier("dogeRpcServiceImpl")
    private IRpcService dogeService;

    @Resource
    @Qualifier("darkRpcServiceImpl")
    private IRpcService rpcService;

    @Resource
    @Qualifier("btcRpcServiceImpl")
    private IRpcService btcRpcService;

    @Test
    public void testGetTransactionList() {
        List<Transaction> transList = btcRpcService.getTransactionList("0000000000000000002ad091cfbf703e1d44ae118cdd93d66624433fd4bd3c25");
        System.out.println(transList.size());
    }

    @Test
    public void testDogeGetTransList(){
        List<Transaction> transList = dogeService.getTransactionList("793136f1e8a22c9823a9dbb25207446b86bd4c2bf153c86bc3d6e751ef019c77");

        for(Transaction t:transList){
            System.out.println(t);
        }
    }
}
