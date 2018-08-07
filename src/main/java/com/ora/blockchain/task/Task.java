package com.ora.blockchain.task;

import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.rpc.IRpcService;
import com.ora.blockchain.utils.BlockchainUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Task {

    public static final int BLOCK_DEPTH = 6;

    public void task(String database,IBlockService blockService,IRpcService rpcService) {
        log.info("********************" + database + " Job start......************************");
        long start = System.currentTimeMillis();
        List<Block> dbBlockList = blockService.queryBlockList(database, null, BLOCK_DEPTH);
        if (!BlockchainUtil.isDistinctCollection(dbBlockList)) {
            List<String> blockHashList = dbBlockList.stream().map(Block::getBlockHash).collect(Collectors.toList());
            blockService.deleteBlockByBlockHash(database,blockHashList);
            dbBlockList = blockService.queryBlockList(database, null, BLOCK_DEPTH);
        }

        if (null == dbBlockList || dbBlockList.isEmpty()) {
            List<Block> blockList = rpcService.getPreviousBlockList(BLOCK_DEPTH, null);
            for (Block block : blockList) {
                blockService.insertBlock(database, block);
            }
            return;
        }
        List<Block> blockList = rpcService.getPreviousBlockList(BLOCK_DEPTH, null);
        blockService.updateBlock(database, dbBlockList, blockList);

        while (true) {
            blockList = rpcService.getPreviousBlockList(BLOCK_DEPTH, blockList.get(blockList.size() - 1).getPreviousBlockHash());
            dbBlockList = blockService.queryBlockList(database, blockList.get(0).getHeight(), BLOCK_DEPTH);
            if (null == dbBlockList || dbBlockList.isEmpty() || BlockchainUtil.isEqualCollection(blockList, dbBlockList)) {
                break;
            }
            try {
                blockService.updateBlock(database, dbBlockList, blockList);
            } catch (DuplicateKeyException e) {
                break;
            }
        }
        long end = System.currentTimeMillis();
        log.info(String.format("*********************" + database + " Job end(spent : %s)*****************************", end - start));
    }
}
