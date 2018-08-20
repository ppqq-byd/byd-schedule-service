package com.ora.blockchain.mybatis.mapper.transaction;

import com.ora.blockchain.mybatis.entity.transaction.EthereumTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import com.ora.blockchain.mybatis.entity.transaction.EthereumERC20;

@Mapper
public interface EthereumERC20Mapper {
    int insert(@Param("pojo") EthereumERC20 pojo);

    int insertSelective(@Param("pojo") EthereumERC20 pojo);

    int insertList(@Param("pojos") List<EthereumERC20> pojo);

    int update(@Param("pojo") EthereumERC20 pojo);

    List<EthereumERC20> queryERC20ByContractAddress(@Param("database")String database,
                                                    @Param("txList")List<EthereumTransaction> txList);

    List<EthereumERC20> queryERC20(@Param("database")String database);


}
