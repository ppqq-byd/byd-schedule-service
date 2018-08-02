package com.ora.blockchain.mybatis.mapper.output;

import com.ora.blockchain.mybatis.entity.output.Output;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OutputMapper {
    public void insertOutput(@Param("database") String database, @Param("pojo") Output record);

    public void insertOutputList(@Param("database") String database, @Param("outputList") List<Output> outputList);

    public void updateOutput(@Param("database") String database, @Param("status") Integer status, @Param("transactionTxid") String transactionTxid, @Param("n") Integer n);

    public void deleteOutput(@Param("database") String database,@Param("blockHash") String blockHash);
}