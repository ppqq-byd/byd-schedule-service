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
@ScheduledJob(name = "bchJob", cronExp = "*/30 * * * * ?")//10 minutes
@DisallowConcurrentExecution
public class BchJob implements Job {
    private static final Long BLOCK_HEIGHT = 8970L;

    @Resource
    @Qualifier("bchBlockScanner")
    private IBlockScanner scanner;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("********************Bch Scanner Job start......************************");
        long start = System.currentTimeMillis();
        try {
//            scanner.scanBlock(BLOCK_HEIGHT,CoinType.BCH.name());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Bch Scanner JOb error! " + e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info(String.format("*********************Bch Scanner Job end(spent : %s)*****************************", end - start));
    }
}
