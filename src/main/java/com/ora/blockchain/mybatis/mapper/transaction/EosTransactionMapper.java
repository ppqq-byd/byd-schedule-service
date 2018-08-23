package com.ora.blockchain.mybatis.mapper.transaction;

import com.ora.blockchain.mybatis.entity.transaction.EosTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface EosTransactionMapper {
    public void insertTransactionList(@Param("database") String database, @Param("transactionList") List<EosTransaction> transactionList);
}