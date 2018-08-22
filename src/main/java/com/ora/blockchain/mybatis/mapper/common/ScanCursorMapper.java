package com.ora.blockchain.mybatis.mapper.common;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import com.ora.blockchain.mybatis.entity.common.ScanCursor;

@Mapper
public interface ScanCursorMapper {
    int insert(@Param("pojo") ScanCursor pojo,@Param("database") String database);

    int insertSelective(@Param("pojo") ScanCursor pojo,@Param("database") String database);

    int insertList(@Param("pojos") List<ScanCursor> pojo,@Param("database") String database);

    int update(@Param("pojo") ScanCursor pojo,@Param("database") String database);

    ScanCursor getEthereumNotConfirmScanCursor(@Param("database") String database);

    void deleteCursorByBlockNumber(@Param("database") String database,@Param("blockNumber")Long blockNumber);
}
