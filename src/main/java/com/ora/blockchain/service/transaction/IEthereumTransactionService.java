package com.ora.blockchain.service.transaction;

import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.util.List;

public interface IEthereumTransactionService {

    public void inserNewBlock(Long initBlockNumber);

    public void confirmBlock();

}
