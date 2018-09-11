package com.ora.blockchain.mybatis.mapper.wallet;

import com.ora.blockchain.mybatis.entity.wallet.Wallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBind;

@Mapper
public interface WalletAccountBindMapper {
    int insert(@Param("pojo") WalletAccountBind pojo);

    int insertSelective(@Param("pojo") WalletAccountBind pojo);

    int insertList(@Param("pojos") List<WalletAccountBind> pojo);

    int update(@Param("pojo") WalletAccountBind pojo);

    List<WalletAccountBind> queryWalletAccountBindByCoinType(@Param("coinType")String coinType);

    List<WalletAccountBind> queryWalletByAddress(@Param("addressList") Set<String> addressList);

    WalletAccountBind queryEthWalletByAddress(@Param("address") String address,@Param("coinType")String coinType);

}

