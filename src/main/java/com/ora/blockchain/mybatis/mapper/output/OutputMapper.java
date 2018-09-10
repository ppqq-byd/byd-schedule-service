package com.ora.blockchain.mybatis.mapper.output;

import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBalance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OutputMapper {

    public void insertOutputList(@Param("database") String database, @Param("outputList") List<Output> outputList);

    public void updateOutputByTxid(@Param("database") String database,@Param("status") Integer status,@Param("txidList") List<String> txidList);

    public void updateOutput(@Param("database") String database, @Param("status") Integer status, @Param("transactionTxid") String transactionTxid, @Param("n") Integer n);

    public void updateOutputBatch(@Param("database") String database, @Param("status") Integer status, @Param("outputList") List<Output> outputList);

    public Output queryOutputByPrimary(@Param("database") String database, @Param("transactionTxid") String txid, @Param("n") Integer n);

    public List<Long> queryAccountByTransactionTxid(@Param("database") String database, @Param("txidList") List<String> txidList);

    public List<String> queryAddressByTransactionTxid(@Param("database") String database, @Param("txidList") List<String> txidList);

    public List<WalletAccountBalance> queryTotalBalance(@Param("database") String database, @Param("accountList") List<Long> accountList);
}