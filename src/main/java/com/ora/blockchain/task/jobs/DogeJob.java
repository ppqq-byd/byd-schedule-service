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
@ScheduledJob(name = "dogeJob", cronExp = "*/30 * * * * ?")
@DisallowConcurrentExecution
public class DogeJob implements Job {
    private static final Long BLOCK_HEIGHT = 1L;

    @Resource
    @Qualifier("dogeBlockScanner")
    private IBlockScanner scanner;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("********************Doge Scanner Job start......************************");
        long start = System.currentTimeMillis();
        try {
//            scanner.scanBlock(BLOCK_HEIGHT,CoinType.DOGE.name());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Doge Scanner JOb error! " + e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info(String.format("*********************Doge Scanner Job end(spent : %s)*****************************", end - start));
    }
}
