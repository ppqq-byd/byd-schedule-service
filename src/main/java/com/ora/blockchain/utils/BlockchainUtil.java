package com.ora.blockchain.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BlockchainUtil {
    private static final int EXPECTED_INSERTIONS = 4000000;
    private static BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), EXPECTED_INSERTIONS);

    public static BloomFilter<String> getBloomFilter(List<String> stringList) {
        if (null != stringList && !stringList.isEmpty()) {
            for (String str : stringList) {
                bloomFilter.put(str);
            }
        }
        return bloomFilter;
    }

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

    public static List<String> getAddress(List<Transaction> transactionList){
        List<Output> outputList = new ArrayList<>();
        for(Transaction t : transactionList){
            if(null != t && null != t.getOutputList() && !t.getOutputList().isEmpty()){
                outputList.addAll(t.getOutputList());
            }
        }
        return outputList.stream().map(Output :: getScriptPubKeyAddresses).collect(Collectors.toList());
    }
}
