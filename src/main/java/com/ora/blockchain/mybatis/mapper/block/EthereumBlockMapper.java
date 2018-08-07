package com.ora.blockchain.mybatis.mapper.block;


import com.ora.blockchain.mybatis.entity.block.EthereumBlock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EthereumBlockMapper {

    public Long queryMaxBlockInDb(@Param("database") String database);

    public Long queryMaxConfirmBlockInDb(@Param("database") String database);

    public void insertBlockList(@Param("database") String database, @Param("blockList") List<EthereumBlock> transactionList);

    public List<EthereumBlock> queryPreEthBlocks(@Param("database") String database,
                                                 @Param("fromNumber")Long from,
                                                 @Param("toNumber")Long to);

    public void updateSetConfirmStatusByHash(
            @Param("confirmNumber") Integer confirmNumber,
            @Param("hashList")List<String> hash);

    public void updateByBlockNumber(@Param("pojo") EthereumBlock pojo);
}
