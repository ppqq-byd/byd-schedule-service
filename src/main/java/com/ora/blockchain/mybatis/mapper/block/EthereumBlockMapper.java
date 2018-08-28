package com.ora.blockchain.mybatis.mapper.block;


import com.ora.blockchain.mybatis.entity.block.EthereumBlock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EthereumBlockMapper {

    public Long queryMaxBlockInDb(@Param("database") String database);

    public Long queryMinBlockInDb(@Param("database") String database);

    public void insertBlock(@Param("database") String database, @Param("pojo") EthereumBlock pojo);

    public void insertBlockList(@Param("database") String database, @Param("blockList") List<EthereumBlock> blockList);

    public List<EthereumBlock> queryPreEthBlocks(@Param("database") String database,
                                                 @Param("fromNumber")Long from,
                                                 @Param("toNumber")Long to);

    public EthereumBlock queryEthBlockByBlockNumber(@Param("database") String database,
                                                    @Param("number")Long number);


    public void updateByBlockNumber(@Param("database") String database,@Param("pojo") EthereumBlock pojo);

    public void deleteBlockByBlockNumber(@Param("database") String database,
                                       @Param("blockNumber") Long blockNumber);

}
