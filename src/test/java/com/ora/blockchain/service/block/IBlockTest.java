package com.ora.blockchain.service.block;

import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.service.rpc.IRpcService;
import com.ora.blockchain.utils.BlockchainUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
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
public class IBlockTest {
    @Autowired
    private IBlockService blockService;
    @Autowired
    private IRpcService rpcService;

    private String database;
    private Block block;

    @Before
    public void init() {
        database = "dark";
        block = new Block();
        block.setBlockHash("000000000000001ee6feea1aef4caf767222a18de463b6e13e71c639f3c1ed2f");
        block.setPreviousBlockHash("000000000000000ca28709fc6f6000ba998b06d55c6b1e7aea21e0435fb62eb8");
        block.setNextBlockHash("0000000000000029ebaf37f9bdefbe8ba5e3e3294c5bfd4e8cada0c313e3474f");
        block.setMerkleroot("d67d9a7afbb09e6b48668af9e1abac6291878c42862d02481d55ebf39459be0c");
        block.setChainwork("0000000000000000000000000000000000000000000008e803e5fe7ebca6865c");
        block.setSize(2431L);
        block.setHeight(904833L);
        block.setVersion(536870916L);
        block.setTime(1531815344L);
        block.setMedianTime(1531814791L);
        block.setBits("194bf2e2");
        block.setNonce(3993790313L);
        block.setDifficulty("56549991.12853394");
    }

//    @Test
    public void testInsertBlock() {
//        blockService.insertBlock(database,block);
        List<Block> blockList = rpcService.getPreviousBlockList(5, null);
        for (Block b : blockList) {
            blockService.insertBlock(database, b);
        }
    }
//    @Test
    public void testUpdateBlock(){
        List<Block> blockList = rpcService.getPreviousBlockList(5, null);
        blockService.updateBlock(database,blockList);
    }

    @Test
    public void testDarkTask(){
        List<Block> dbBlockList = blockService.queryBlockList("dark", null,6);
        if (null == dbBlockList || dbBlockList.isEmpty()) {
            List<Block> blockList = rpcService.getPreviousBlockList(6, null);
            for (Block block : blockList) {
                blockService.insertBlock("dark", block);
            }
        }else{
            List<Block> blockList = rpcService.getPreviousBlockList(6,null);
            blockService.updateBlock("dark",dbBlockList,blockList);
            while(true){
                blockList = rpcService.getPreviousBlockList(1,blockList.get(blockList.size()-1).getPreviousBlockHash());
                dbBlockList = blockService.queryBlockList("dark",blockList.get(0).getHeight(),1);
                if(BlockchainUtil.isEqualCollection(blockList,dbBlockList)){
                    break;
                }
                blockService.updateBlock("dark",dbBlockList,blockList);
            }
        }
    }

    @Test
    public  void testLTCTask(){
        List<Block> dbBlockList = blockService.queryBlockList("ltc", null,6);
        if (null == dbBlockList || dbBlockList.isEmpty()) {
            List<Block> blockList = rpcService.getPreviousBlockList(6, null);
            for (Block block : blockList) {
                blockService.insertBlock("ltc", block);
            }
        }else{
            List<Block> blockList = rpcService.getPreviousBlockList(6,null);
            blockService.updateBlock("ltc",dbBlockList,blockList);
            while(true){
                blockList = rpcService.getPreviousBlockList(1,blockList.get(blockList.size()-1).getPreviousBlockHash());
                dbBlockList = blockService.queryBlockList("ltc",blockList.get(0).getHeight(),1);
                if(BlockchainUtil.isEqualCollection(blockList,dbBlockList)){
                    break;
                }
                blockService.updateBlock("ltc",dbBlockList,blockList);
            }
        }
    }
}
