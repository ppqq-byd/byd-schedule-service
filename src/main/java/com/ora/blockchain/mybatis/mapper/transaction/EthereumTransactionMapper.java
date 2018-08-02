package com.ora.blockchain.mybatis.mapper.transaction;

import com.ora.blockchain.mybatis.entity.transaction.EthereumTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EthereumTransactionMapper {

    public void insertTransaction(@Param("database") String database,
                                  @Param("pojo") EthereumTransaction record);


}
