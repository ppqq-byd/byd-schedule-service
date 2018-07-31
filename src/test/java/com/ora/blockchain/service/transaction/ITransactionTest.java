package com.ora.blockchain.service.transaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.ora.blockchain.mybatis.entity.input.Input;
import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.entity.transaction.Transaction;
import com.ora.blockchain.service.rpc.IRpcService;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ITransactionTest {
    @Autowired
    private ITransactionService transService;
    @Autowired
    private IRpcService rpcService;

    private String database;
    private List<Output> outputList;
    private List<Input> inputList;
    private Transaction trans;

    @Before
    public void init(){
        database = "dark";
//        trans = new Transaction();
//        trans.setTxid("bc70250adbb261ebe886642463941d2e84ad22d84c1c908fb34f01466ce05597");;
//        trans.setHex("010000000189bf632c74b4e48790dfc012a02bfeb317699edb925eff1b25ac6fac636f5218000000006b483045022100d63a843418209f34fdf9bdbe13eec70c8d6fd07b3eb6fb212c7d1e21a316b67902200846dc37dc26c5ad909ae06bc0d5e4dc9bcd3e6a05c7f66fa911a451b4ae90f00121023434d5c3385fd51eaeb4d7d2d14e112b1ae1cabb68c79d80906f85e8a0929832ffffffff0240420f00000000001976a914038b8f58933ace9d3f6611250c69cb16f2f8dbd688ac00b19e00000000001976a9146ec71a502e65a08f9870da161ef08e62d867336a88ac00000000");
//        trans.setSize(226L);
//        trans.setVersion(1L);
//        trans.setLocktime(0L);
//        trans.setHeight(904139L);
//        trans.setTime(1531707350L);
//        trans.setBlockHash("0000000000000030a3d072518d3e9b965057314858ed599be733457cd282fd8e");
//        trans.setBlockTime(1531707350L);
//        inputList = new ArrayList<>();
//        Input input = new Input();
//        input.setTxid("18526f63ac6fac251bff5e92db9e6917b3fe2ba012c0df9087e4b4742c63bf89");
//        input.setVout(0);
//        input.setScriptSigAsm("3045022100d63a843418209f34fdf9bdbe13eec70c8d6fd07b3eb6fb212c7d1e21a316b67902200846dc37dc26c5ad909ae06bc0d5e4dc9bcd3e6a05c7f66fa911a451b4ae90f0");
//        input.setScriptSigHex("483045022100d63a843418209f34fdf9bdbe13eec70c8d6fd07b3eb6fb212c7d1e21a316b67902200846dc37dc26c5ad909ae06bc0d5e4dc9bcd3e6a05c7f66fa911a451b4ae90f00121023434d5c3385fd51eaeb4d7d2d14e112b1ae1cabb68c79d80906f85e8a0929832");
//        input.setTransactionTxid("bc70250adbb261ebe886642463941d2e84ad22d84c1c908fb34f01466ce05597");
//        input.setTransactionTxid("bc70250adbb261ebe886642463941d2e84ad22d84c1c908fb34f01466ce05597");
//        input.setSequence(4294967295L);
//        inputList.add(input);
//        outputList = new ArrayList<>();
//        Output o1 = new Output();
//        o1.setValue(0.01);
//        o1.setValueSat(1000000L);
//        o1.setN(0);
//        o1.setScriptPubKeyAsm("OP_DUP OP_HASH160 038b8f58933ace9d3f6611250c69cb16f2f8dbd6 OP_EQUALVERIFY OP_CHECKSIG");
//        o1.setScriptPubKeyHex("76a914038b8f58933ace9d3f6611250c69cb16f2f8dbd688ac");
//        o1.setScriptPubKeyReqSigs(1);
//        o1.setScriptPubKeyType("pubkeyhash");
//        o1.setScriptPubKeyAddresses("Xb1b3d9DPthunfBTHoSQT4hUVgLaYawuPV");
//        o1.setTransactionTxid("bc70250adbb261ebe886642463941d2e84ad22d84c1c908fb34f01466ce05597");
//        Output o2 = new Output();
//        o2.setValue(0.104);
//        o2.setValueSat(10400000L);
//        o2.setN(1);
//        o2.setScriptPubKeyAsm("OP_DUP OP_HASH160 6ec71a502e65a08f9870da161ef08e62d867336a OP_EQUALVERIFY OP_CHECKSIG");
//        o2.setScriptPubKeyHex("76a9146ec71a502e65a08f9870da161ef08e62d867336a88ac");
//        o2.setScriptPubKeyReqSigs(1);
//        o2.setScriptPubKeyType("pubkeyhash");
//        o2.setScriptPubKeyAddresses("XknagFyCMBmXp3mii4YNYvgy4cFbtaQCLg");
//        o2.setTransactionTxid("bc70250adbb261ebe886642463941d2e84ad22d84c1c908fb34f01466ce05597");
//        outputList.add(o1);
//        outputList.add(o2);
//        trans.setOutputList(outputList);
//        trans.setInputList(inputList);
    }

    @Test
    public void testInsertTransaction(){
        List<Transaction> transList = rpcService.getTransactionList(1,"8c5cafc246d6fd99acd080834e4a54f8a7a4983878a4a7e44c9d989ece65ef5c");
        System.out.println(transList);
//        for(Transaction trans:transList){
//            transService.insertTransaction(database,trans);
//        }
    }
}
