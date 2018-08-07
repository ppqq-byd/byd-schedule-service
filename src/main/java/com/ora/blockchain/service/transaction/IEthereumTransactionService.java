package com.ora.blockchain.service.transaction;

import com.ora.blockchain.mybatis.entity.transaction.Transaction;

import java.util.List;

public interface IEthereumTransactionService {

    public void inserNewBlock();

    public void confirmBlock();

}
