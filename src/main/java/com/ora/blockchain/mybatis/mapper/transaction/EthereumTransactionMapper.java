package com.ora.blockchain.mybatis.mapper.transaction;

import com.ora.blockchain.mybatis.entity.block.EthereumBlock;
import com.ora.blockchain.mybatis.entity.transaction.EthereumTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EthereumTransactionMapper {

    public void insertTransaction(@Param("database") String database,
                                  @Param("pojo") EthereumTransaction record);


    public void updateTransacion(@Param("database") String database,
                                 @Param("blockHeight") Long blockNumber,
                                 @Param("blockHash") String blockHash
                                 );

    public Long queryMaxBlockOfTxInDb(@Param("database") String database);

    public List<EthereumTransaction> queryIsolatedBlockTx(@Param("database") String database);


    public void insertTxList(@Param("database") String database,
                             @Param("txList") List<EthereumTransaction> txList);


    public void deleteTxByTxhash(@Param("database") String database,
                                 @Param("txList") List<String> txList);

    public void batchUpdate(@Param("database") String database,
                                                         @Param("txList") List<EthereumTransaction> txList);

    public List<EthereumTransaction> queryTxInDb(@Param("database") String database,
                                                           @Param("txList") List<EthereumTransaction> txList);

}
