package com.ora.blockchain.mybatis.mapper.transaction;

import com.ora.blockchain.mybatis.entity.eth.EthereumTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import com.ora.blockchain.mybatis.entity.eth.EthereumERC20;

@Mapper
public interface EthereumERC20Mapper {
    int insert(@Param("database")String database,@Param("pojo") EthereumERC20 pojo);

    int insertSelective(@Param("database")String database,@Param("pojo") EthereumERC20 pojo);

    int insertList(@Param("database")String database,@Param("pojos") List<EthereumERC20> pojo);

    int update(@Param("database")String database,@Param("pojo") EthereumERC20 pojo);

    List<EthereumERC20> queryERC20ByContractAddress(@Param("database")String database,
                                                    @Param("txList")List<EthereumTransaction> txList);

    List<EthereumERC20> queryERC20(@Param("database")String database);


}
