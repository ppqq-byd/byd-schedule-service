package com.ora.blockchain.mybatis.mapper.input;

import com.ora.blockchain.mybatis.entity.input.Input;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InputMapper {

    public void insertInput(@Param("database") String database, @Param("pojo") Input record);

    public void insertInputList(@Param("database") String database, @Param("inputList") List<Input> inputList);
}