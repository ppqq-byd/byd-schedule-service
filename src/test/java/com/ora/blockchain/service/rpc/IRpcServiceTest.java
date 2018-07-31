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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IRpcServiceTest {
    @Autowired
    private IRpcService rpcService;

//    @Test
    public void testGetTransactionList() {
        List<Transaction> transList = rpcService.getTransactionList(1, "000000000000001d9bf0f326224dd8841cb792903a8f5070d8af04680ce4711c");
        for (Transaction t : transList) {
            System.out.println(t.toString());
        }
    }
    @Test
    public void testGetBlockList(){
//        List<Block> blockList = rpcService.getPreviousBlockList(6,null);
//        for(Block block:blockList){
//            System.out.println(block.toString());
//        }
        System.out.println("------------------------------------------------------------");
        List<Block> blockList = rpcService.getNextBlockList(3,"00000000000000351fa575e134a71ba05e020b57310448f2f6718b6df1ce790e");
        for(Block block:blockList){
            System.out.println(block.toString());
        }
    }
}
