package com.ora.blockchain.mybatis.mapper.output;

import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBalance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OutputMapper {
    public void insertOutput(@Param("database") String database, @Param("pojo") Output record);

    public void insertOutputList(@Param("database") String database, @Param("outputList") List<Output> outputList);

    public void updateOutput(@Param("database") String database, @Param("status") Integer status, @Param("transactionTxid") String transactionTxid, @Param("n") Integer n);

    public void updateOutputBatch(@Param("database") String database, @Param("status") Integer status, @Param("outputList") List<Output> outputList);

    public void updateWalletAccountId(@Param("database") String database, @Param("walletAccountId") Long walletAccountId, @Param("transactionTxid") String transactionTxid, @Param("n") Integer n);

    public List<Output> queryOutputByWalletAccount(@Param("database") String database);

    public Output queryOutputByPrimary(@Param("database") String database, @Param("transactionTxid") String txid, @Param("n") Integer n);

    public List<Output> queryUTXOList(@Param("database") String database, @Param("walletAccountId") Long walletAccountId);

    public List<Long> queryAccountByTransactionTxid(@Param("database") String database, @Param("txidList") List<String> txidList);

    public List<WalletAccountBalance> queryTotalBalance(@Param("database") String database, @Param("accountList") List<Long> accountList, @Param("nHeight") Long nHeight, @Param("cHeight") Long cHeight);
}