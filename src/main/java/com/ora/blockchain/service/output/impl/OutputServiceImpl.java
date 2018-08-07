package com.ora.blockchain.service.output.impl;

import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.mapper.output.OutputMapper;
import com.ora.blockchain.service.output.IOutputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutputServiceImpl implements IOutputService {
    @Autowired
    private OutputMapper outputMapper;

    @Override
    public void updateWalletAccountId(String database, Long walletAccountId, String transactionTxid, Integer n) {
        outputMapper.updateWalletAccountId(database,walletAccountId,transactionTxid,n);
    }

    @Override
    public List<Output> queryOutputByWalletAccount(String database) {
        return outputMapper.queryOutputByWalletAccount(database);
    }
}
