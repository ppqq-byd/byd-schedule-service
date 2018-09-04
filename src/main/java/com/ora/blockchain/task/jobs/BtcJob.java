package com.ora.blockchain.task.jobs;


import com.ora.blockchain.constants.CoinType;
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
@ScheduledJob(name = "btcJob", cronExp = "*/30 * * * * ?")
@DisallowConcurrentExecution
public class BtcJob implements Job {
    private static final Long BTC_BLOCK_HEIGHT = 8970L;

    @Resource
    @Qualifier("btcBlockScanner")
    private IBlockScanner scanner;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("********************Btc Scanner Job start......************************");
        long start = System.currentTimeMillis();
        try {
            scanner.scanBlock(BTC_BLOCK_HEIGHT,CoinType.BTC.name());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Btc Scanner JOb error! " + e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info(String.format("*********************Btc Scanner Job end(spent : %s)*****************************", end - start));
    }
}
