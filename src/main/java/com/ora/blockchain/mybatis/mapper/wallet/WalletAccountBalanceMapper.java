package com.ora.blockchain.mybatis.mapper.wallet;

import com.ora.blockchain.mybatis.entity.wallet.ERC20Sum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBalance;

@Mapper
public interface WalletAccountBalanceMapper {
    int insert(@Param("pojo") WalletAccountBalance pojo);

    int insertSelective(@Param("pojo") WalletAccountBalance pojo);

    int insertList(@Param("pojos") List<WalletAccountBalance> pojo);

    int update(@Param("pojo") WalletAccountBalance pojo);

    WalletAccountBalance findBalanceOfCoinByAddressAndCointype(@Param("address")String address,
                                                         @Param("coinType")String coinType);

    List<WalletAccountBalance> findTokenBalanceByAddressAndCointype(@Param("address")String address,
                                                         @Param("coinType")String coinType);

    WalletAccountBalance findBalanceByContractAddressAndCoinType(
            @Param("database")String database,@Param("coinType")String coinType,
            @Param("contractAddress")String contractAddress,
            @Param("accountAddress")String accountAddress);

    ERC20Sum findERC20OutSumByAddressAndTokenId(@Param("address")String accountAddress,
                                                @Param("ercId") Integer ercId);


    BigInteger findERC20InSumByAddressAndTokenId(@Param("address")String accountAddress,
                                                 @Param("ercId") Integer ercId);

    ERC20Sum findEthOutSumByAddress(@Param("address")String accountAddress);

    BigInteger findEthInSumByAddress(@Param("address")String accountAddress);

    int updateBatch(@Param("walletAccountBalanceList") List<WalletAccountBalance> walletAccountBalanceList);

    List<WalletAccountBalance> findBlanceByEthAddressAndContractAddress(
            @Param("ethAddress") String ethAddress,
            @Param("contractAddressList")List<String> contractAddressList);
}
