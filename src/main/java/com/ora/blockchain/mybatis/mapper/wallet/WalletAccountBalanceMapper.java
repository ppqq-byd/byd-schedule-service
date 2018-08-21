package com.ora.blockchain.mybatis.mapper.wallet;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBalance;

@Mapper
public interface WalletAccountBalanceMapper {
    int insert(@Param("pojo") WalletAccountBalance pojo);

    int insertSelective(@Param("pojo") WalletAccountBalance pojo);

    int insertList(@Param("pojos") List<WalletAccountBalance> pojo);

    int update(@Param("pojo") WalletAccountBalance pojo);

    int updateBatch(@Param("walletAccountBalanceList") List<WalletAccountBalance> walletAccountBalanceList);
}
