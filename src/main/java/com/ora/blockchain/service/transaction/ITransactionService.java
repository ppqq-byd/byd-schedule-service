package com.ora.blockchain.service.transaction;

import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.mybatis.entity.input.Input;
import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.entity.transaction.Transaction;

import java.util.List;

public interface ITransactionService {
    public void insertTransaction(String database, Transaction transaction);

    public void insertTransactionList(String database, List<Transaction> transactionList);

}
