package com.ora.blockchain.utils;

import com.ora.blockchain.mybatis.entity.block.Block;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BlockchainUtil {
    public static boolean containsAny(List<Block> blockList1, List<Block> blockList2) {
        if (null == blockList1 || null == blockList2) {
            return false;
        }
        List<String> hashList1 = blockList1.stream().map(Block::getBlockHash).collect(Collectors.toList());
        List<String> hashList2 = blockList2.stream().map(Block::getBlockHash).collect(Collectors.toList());
        return CollectionUtils.containsAny(blockList1, blockList2);
    }
    public static boolean isEqualCollection(List<Block> blockList1, List<Block> blockList2){
        if (null == blockList1 || null == blockList2) {
            return false;
        }
        List<String> hashList1 = blockList1.stream().map(Block::getBlockHash).collect(Collectors.toList());
        List<String> hashList2 = blockList2.stream().map(Block::getBlockHash).collect(Collectors.toList());
        return CollectionUtils.isEqualCollection(hashList1,hashList2);
    }
}
