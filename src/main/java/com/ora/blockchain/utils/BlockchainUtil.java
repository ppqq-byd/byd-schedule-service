package com.ora.blockchain.utils;

import com.ora.blockchain.mybatis.entity.block.Block;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BlockchainUtil {
    public static boolean isEqualCollection(List<Block> blockList1, List<Block> blockList2) {
        if (null == blockList1 || null == blockList2) {
            return false;
        }
        List<Long> hashList1 = blockList1.stream().map(Block::getHeight).collect(Collectors.toList());
        List<Long> hashList2 = blockList2.stream().map(Block::getHeight).collect(Collectors.toList());
        return CollectionUtils.isEqualCollection(hashList1, hashList2);
    }

    public static boolean isDistinctCollection(List<Block> blockList) {
        if (null != blockList && !blockList.isEmpty()) {
            List<Long> list = blockList.stream().map(Block::getHeight).distinct().collect(Collectors.toList());
            return list.size() == blockList.size();
        }
        return true;
    }
}
