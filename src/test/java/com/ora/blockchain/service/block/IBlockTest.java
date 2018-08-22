package com.ora.blockchain.service.block;

import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.mybatis.entity.wallet.Wallet;
import com.ora.blockchain.mybatis.mapper.wallet.WalletMapper;
import com.ora.blockchain.service.blockscanner.IBlockScanner;
import com.ora.blockchain.service.rpc.IRpcService;
import com.ora.blockchain.task.Task;
import com.ora.blockchain.utils.BlockchainUtil;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private Task task;
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

    @Test
    public void testBtcScanner(){
        for(int i=0 ;i<1 ;i++){
            try {
                btcScanner.scanBlock(536051L-1,CoinType.getDatabase(CoinType.BTC.name()));
                btcScanner.updateAccount(CoinType.BTC.name());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testDarkScanner(){
        try {
            darkScanner.scanBlock(921662L,CoinType.getDatabase(CoinType.DARK.name()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void deleteBlock(){
        btcBlockService.deleteByHeight(CoinType.getDatabase(CoinType.BTC.name()),536051L);
    }

    @Test
    public void queryBlock(){
        Block block = btcBlockService.queryLastBlock("coin_btc");
        System.out.println(block.getBlockHash());
    }
}
