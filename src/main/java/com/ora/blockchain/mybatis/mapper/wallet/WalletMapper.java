package com.ora.blockchain.mybatis.mapper.wallet;

import com.ora.blockchain.mybatis.entity.wallet.Wallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WalletMapper {
    public Wallet queryWallet(@Param("address") String address);

    public List<Wallet> queryWalletByAddress(@Param("addressList") List<String> addressList);
}
