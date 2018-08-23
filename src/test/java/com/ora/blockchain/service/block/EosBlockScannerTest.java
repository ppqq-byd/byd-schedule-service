package com.ora.blockchain.service.block;

import client.EosApiClientFactory;
import client.EosApiRestClient;
import client.domain.response.chain.Block;
import client.domain.response.history.transaction.Transaction;
import com.ora.blockchain.service.blockscanner.impl.eosfamily.EosBlockScanner;
import com.ora.blockchain.utils.EosUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
public class EosBlockScannerTest {

    @Resource
    @Qualifier("eosBlockScanner")
    private EosBlockScanner eosScanner;

    @Test
    public void testEosScanner(){
        try {
            eosScanner.scanBlock(536051L-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEosApiRestClient(){
        EosApiRestClient eosApiRestClient = EosApiClientFactory.newInstance("http://127.0.0.1:8888").newRestClient();
        Block block = eosApiRestClient.getBlock("12083176");
        //Block block = eosApiRestClient.getBlock("4038475");
        //System.out.println(block);

        Transaction t = block.getTransactions()[0];
        String id = EosUtils.getValue((Map)(t.getTrx()),"id" );
        System.out.println(id);

        List actionsList =  EosUtils.getValueList((Map)(t.getTrx()),"transaction/actions" );
        System.out.println(actionsList);
        String actions = String.join("+", EosUtils.listToArrayList(actionsList));
        System.out.println(actions);
        String account = EosUtils.getValue((Map)actionsList.get(0), "account");
        System.out.println(account);
        List authorizationList = EosUtils.getValueList((Map)actionsList.get(0),"authorization");
        String authorization = String.join("+", EosUtils.listToArrayList(authorizationList));
        System.out.println(authorization);
        String from = EosUtils.getValue((Map)actionsList.get(0), "data/from");
        System.out.println(from);

        List signaturesList = EosUtils.getValueList((Map)(t.getTrx()), "signatures");
        System.out.println(signaturesList);
        String signatures = String.join("+", signaturesList);
        System.out.println(signatures);

        String compression = EosUtils.getValue((Map)(t.getTrx()), "compression");
        System.out.println(compression);

        String packed_context_free_data = EosUtils.getValue((Map)(t.getTrx()), "packed_context_free_data");
        System.out.println(packed_context_free_data);

        List context_free_data_list = EosUtils.getValueList((Map)(t.getTrx()), "context_free_data");
        System.out.println(context_free_data_list);
        String context_free_data = String.join("+", context_free_data_list);
        System.out.println(context_free_data);

        String packed_trx = EosUtils.getValue((Map)(t.getTrx()), "packed_trx");
        System.out.println(packed_trx);

        String expiration = EosUtils.getValue((Map)(t.getTrx()), "transaction/expiration");
        System.out.println(expiration);

        String ref_block_num = EosUtils.getValue((Map)(t.getTrx()), "transaction/ref_block_num");
        System.out.println(Long.valueOf(ref_block_num));

        //Transaction tx = eosApiRestClient.getTransaction("12083176", "6d9dc93006ab221e08ca5f69668e1e5d64ea1cc559e8c162b0abb527ef6b02ac");
        //System.out.println(tx);
        System.out.println("---------------end------------");
    }
}
