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
@ScheduledJob(name = "btgJob", cronExp = "*/30 * * * * ?")
@DisallowConcurrentExecution
public class BtgJob implements Job {
    private static final Long BLOCK_HEIGHT = 8970L;

    @Resource
    @Qualifier("btgBlockScanner")
    private IBlockScanner scanner;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("********************Btg Scanner Job start......************************");
        long start = System.currentTimeMillis();
        try {
//            scanner.scanBlock(BLOCK_HEIGHT,CoinType.BTG.name());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Btg Scanner JOb error! " + e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info(String.format("*********************Btg Scanner Job end(spent : %s)*****************************", end - start));
    }
}
