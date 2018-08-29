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
@ScheduledJob(name = "LtcJob", cronExp = "0 */1 * * * ?")
@DisallowConcurrentExecution
public class LtcJob implements Job {
    private static final Long LTC_BLOCK_HEIGHT = 1481744L;

    @Resource
    @Qualifier("ltcBlockScanner")
    private IBlockScanner scanner;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("********************Ltc Scanner Job start......************************");
        long start = System.currentTimeMillis();
        try {
//            scanner.scanBlock(LTC_BLOCK_HEIGHT,CoinType.LTC.name());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Ltc Scanner JOb error! " + e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info(String.format("*********************Ltc Scanner Job end(spent : %s)*****************************", end - start));
    }
}
