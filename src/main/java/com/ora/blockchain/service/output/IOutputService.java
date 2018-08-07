package com.ora.blockchain.service.output;

import com.ora.blockchain.mybatis.entity.output.Output;

import java.util.List;

public interface IOutputService {
    public void updateWalletAccountId(String database, Long walletAccountId, String transactionTxid, Integer n);

    public List<Output> queryOutputByWalletAccount(String database);
}
