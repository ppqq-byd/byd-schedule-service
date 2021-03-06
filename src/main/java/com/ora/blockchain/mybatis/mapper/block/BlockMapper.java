package com.ora.blockchain.mybatis.mapper.block;

import com.ora.blockchain.mybatis.entity.block.Block;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BlockMapper {
    public void insertBlock(@Param("database") String database, @Param("pojo") Block block);

    public List<Block> queryBlockList(@Param("database") String database, @Param("height") Long height, @Param("size") int size);

    public void deleteBlockByBlockHash(@Param("database") String database, @Param("blockHash") String blockHash);

    public Block queryLastBlock(@Param("database") String database);

    public Block queryByBlockHeight(@Param("database") String database, @Param("blockHeight") Long blockHeight);
}
