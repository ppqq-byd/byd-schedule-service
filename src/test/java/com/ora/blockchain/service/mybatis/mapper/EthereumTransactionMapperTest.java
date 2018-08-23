package com.ora.blockchain.service.mybatis.mapper;

import com.ora.blockchain.mybatis.entity.block.EthereumBlock;
import com.ora.blockchain.mybatis.entity.eth.EthereumTransaction;
import com.ora.blockchain.mybatis.mapper.block.EthereumBlockMapper;
import com.ora.blockchain.mybatis.mapper.transaction.EthereumTransactionMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest

public class EthereumTransactionMapperTest {

    @Autowired
    private EthereumTransactionMapper txMapper;

    @Autowired
    private EthereumBlockMapper blockMapper;

    @Test
    public void testBatchUpdate(){
        List<EthereumTransaction> list = new ArrayList<>();
        EthereumTransaction tx = new EthereumTransaction();
        tx.setTxId("0xa93afba19da6f6ef0a858da896b7aa92161ce3a289d379a197fc31445a18ee92");
        tx.setStatus(2);
        tx.setBlockHash("xxx");
        tx.setGasPrice("2111111111111111111111121333333333333333132");
        tx.setGasUsed("2111111111111111111111121333333333333333132");
        tx.setBlockHeight(2133112L);
        tx.setId(1L);
        tx.setUpdateTs(new Date());
        list.add(tx);

        txMapper.batchUpdate("coin_eth",list);

    }

    @Test
    public void testInsert(){
        EthereumTransaction tx = new EthereumTransaction();
        tx.setTxId("aaa");
        tx.setFrom("a");
        tx.setTo("b");
        txMapper.insertTransaction("coin_eth",tx);
    }


    @Test
    public void testMaxBlockHeightInDb(){

        long max = blockMapper.queryMaxBlockInDb("coin_eth");
        System.out.println(max);
    }

    @Test
    public void testInsertBlocks(){
        List<EthereumBlock> list = new ArrayList<>();
        EthereumBlock block = new EthereumBlock();
        block.setBlockNumber(2L);
        block.setBlockTime(new Date());
        block.setDifficulty(213132L);
        block.setHash("1231dsf");
        block.setParentHash("sdf");
        list.add(block);
        blockMapper.insertBlockList("coin_eth",list);

    }

    @Test
    public void testInsertTx(){
        List<EthereumTransaction> list =new ArrayList<>();
        EthereumTransaction tx = new EthereumTransaction();
                tx.setBlockHash("aaa");
        tx.setValue("2111111111111111111111121333333333333333132");
        tx.setGasUsed("2111111111111111111111121333333333333333132");
        tx.setGasPrice("2111111111111111111111121333333333333333132");
        tx.setTxId("dsafsd");
        tx.setFrom("a");
        tx.setTo("b");
        list.add(tx);
        txMapper.insertTxList("coin_eth",list);

    }

    @Test
    public void testQueryBlocks(){
        List<EthereumBlock> list =
                blockMapper.queryPreEthBlocks("coin_eth",6095842L,6095852L);

        for(EthereumBlock block:list){
            System.out.println(block.getHash());
        }
    }
}
