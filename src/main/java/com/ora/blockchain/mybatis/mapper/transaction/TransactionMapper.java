package com.ora.blockchain.mybatis.mapper.transaction;

import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface TransactionMapper {
    public void insertTransaction(@Param("database") String database, @Param("pojo") Transaction record);

    public void insertTransactionList(@Param("database") String database, @Param("transactionList") List<Transaction> transactionList);

    public List<Transaction> queryTransactionListByBlockHash(@Param("database") String database, @Param("blockHash") String blockHash);

    public List<Transaction> queryTransactionListByTxid(@Param("database") String database, @Param("txidList") List<String> txidList);

    public void deleteTransactionByBlockHash(@Param("database") String database, @Param("blockHash") String blockHash);

    public void updateTransactionList(@Param("database") String database, @Param("transactionList") List<Transaction> transactionList);

    public List<Transaction> queryTransactionListByTransStatus(@Param("database") String database,@Param("transStatusList") List<Integer> transStatusList);
}