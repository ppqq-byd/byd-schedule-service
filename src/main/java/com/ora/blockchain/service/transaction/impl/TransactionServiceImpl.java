package com.ora.blockchain.service.transaction.impl;

import com.ora.blockchain.mybatis.entity.input.Input;
import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import com.ora.blockchain.mybatis.mapper.input.InputMapper;
import com.ora.blockchain.mybatis.mapper.output.OutputMapper;
import com.ora.blockchain.mybatis.mapper.transaction.TransactionMapper;
import com.ora.blockchain.service.rpc.IRpcService;
import com.ora.blockchain.service.transaction.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImpl implements ITransactionService {

    @Autowired
    private IRpcService rpcService;
    @Autowired
    private TransactionMapper transMapper;
    @Autowired
    private InputMapper inputMapper;
    @Autowired
    private OutputMapper outputMapper;

    @Override
    @Transactional
    public void insertTransaction(String database, Transaction transaction) {
        transMapper.insertTransaction(database, transaction);
        if (null != transaction.getInputList() && !transaction.getInputList().isEmpty()) {
            inputMapper.insertInputList(database, transaction.getInputList());
        }
        if (null != transaction.getOutputList() && !transaction.getOutputList().isEmpty()) {
            outputMapper.insertOutputList(database, transaction.getOutputList());
        }
    }

    @Override
    @Transactional
    public void insertTransactionList(String database, List<Transaction> transactionList) {
        if (null == transactionList || transactionList.isEmpty()) {
            return;
        }
        List<Output> outputList = new ArrayList<>();
        List<Input> inputList = new ArrayList<>();
        for(Transaction t:transactionList){
            outputList.addAll(t.getOutputList());
            inputList.addAll(t.getInputList());
        }
        transMapper.insertTransactionList(database,transactionList);
        inputMapper.insertInputList(database,inputList);
        outputMapper.insertOutputList(database,outputList);
    }
}
