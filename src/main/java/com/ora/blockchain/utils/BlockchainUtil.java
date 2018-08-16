package com.ora.blockchain.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import io.swagger.models.auth.In;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public static int binarySearchIndex(Object[] array, int targetNum) {
        Arrays.sort(array);
       if(((Integer)array[array.length-1]).intValue() <= targetNum)
           return (Integer)array[array.length-1];
       if(((Integer)array[0]).intValue() >= targetNum)
           return (Integer)array[0];
        int left = 0, right = 0;
        for (right = array.length - 1; left != right; ) {
            int midIndex = (right + left) / 2;
            int mid = (right - left);
            int midValue = (Integer) array[midIndex];
            if (targetNum == midValue) {
                return midIndex;
            }
            if (targetNum > midValue) {
                left = midIndex;
            } else {
                right = midIndex;
            }

            if (mid <= 1) {
                break;
            }
        }
        int rightnum = ((Integer) array[right]).intValue();
        int leftnum = ((Integer) array[left]).intValue();
        int ret = Math.abs((rightnum - leftnum) / 2) > Math.abs(rightnum - targetNum) ? rightnum : leftnum;
        System.out.println("和要查找的数：" + targetNum + "最接近的数：" + ret);
        System.out.println(Math.abs((rightnum - leftnum) / 2) > Math.abs(rightnum - targetNum) ? right : left);
        return Math.abs((rightnum - leftnum) / 2) > Math.abs(rightnum - targetNum) ? right : left;
    }

    public static List<Object> search(Object [] array,int targetNum){
        List<Object> res = new ArrayList<>();
        int index = binarySearchIndex(array,targetNum);
        if(((Integer)array[index]).intValue() > targetNum){
            res.add(array[index]);
            return res;
        }
        int total = 0;
        while (total <= targetNum){
            total = total + (Integer)array[index];
            res.add(array[index]);
            index = index -1;
            if(index <= 0){
                break;
            }
        }
        if(total < targetNum)
            return null;
        return res;
    }

    public static void main(String[] args) {
        ArrayList array = new ArrayList();
        array.add(1);
        array.add(5);
        array.add(10);
        array.add(15);
        array.add(25);
        array.add(60);
        array.add(77);

        Collections.sort(array);
        int targetNum= 65;
        System.out.println("和要查找的数："+targetNum+ "最接近的数："+binarySearchIndex(array.toArray(), targetNum));
//        List<Object> list = search(array,10)
    }
}
