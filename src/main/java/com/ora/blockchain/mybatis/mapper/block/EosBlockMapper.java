package com.ora.blockchain.mybatis.mapper.block;

import com.ora.blockchain.mybatis.entity.block.EosBlock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EosBlockMapper {
    public void insertBlock(@Param("database") String database, @Param("pojo") EosBlock block);
    public EosBlock queryLastBlock(@Param("database") String database);
}
