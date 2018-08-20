package com.ora.blockchain.task.jobs;


import com.ora.blockchain.service.blockscanner.IBlockScanner;
import com.ora.blockchain.task.ScheduledJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@ScheduledJob(name = "btcJob", cronExp = "0 */1 * * * ?")
@DisallowConcurrentExecution
public class BtcJob implements Job {
    private static final Long BTC_BLOCK_HEIGHT = 536052L;

    @Resource
    @Qualifier("btcBlockScanner")
    private IBlockScanner btcBlockScanner;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("********************Btc Scanner Job start......************************");
        long start = System.currentTimeMillis();
        try {
//            scanner.scanBlock(BTC_BLOCK_HEIGHT);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Btc Scanner JOb error! " + e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info(String.format("*********************Btc Scanner Job end(spent : %s)*****************************", end - start));
    }
    //TODO 查询出用户未花费的所有UTXO ，根据UTXO计算总金额和被冻结的金额，须和queryUTXOList相同
    /*SELECT value_sat,t.block_hash,t.trans_status,o.`status` FROM `transaction` t,output o
    WHERE o.transaction_txid = t.txid
    AND o.`status` = 1
    AND o.wallet_account_id = 8*/
}
