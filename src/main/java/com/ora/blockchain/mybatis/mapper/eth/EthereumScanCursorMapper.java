package com.ora.blockchain.mybatis.mapper.eth;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import com.ora.blockchain.mybatis.entity.eth.EthereumScanCursor;

@Mapper
public interface EthereumScanCursorMapper {
    int insert(@Param("pojo") EthereumScanCursor pojo);

    int insertSelective(@Param("pojo") EthereumScanCursor pojo);

    int insertList(@Param("pojos") List<EthereumScanCursor> pojo);

    int update(@Param("pojo") EthereumScanCursor pojo);

    EthereumScanCursor getEthereumNotConfirmScanCursor(@Param("database") String database);
}
