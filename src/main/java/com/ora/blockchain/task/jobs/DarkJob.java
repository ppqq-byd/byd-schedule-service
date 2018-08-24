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
@ScheduledJob(name = "darkJob", cronExp = "0 */1 * * * ?")
@DisallowConcurrentExecution
public class DarkJob implements Job {
    private static final Long BLOCK_HEIGHT = 921663L;

    @Resource
    @Qualifier("darkBlockScanner")
    private IBlockScanner scanner;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("********************Dark Scanner Job start......************************");
        long start = System.currentTimeMillis();
        try {
//            scanner.scanBlock(BLOCK_HEIGHT);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Dark Scanner Job error! " + e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info(String.format("*********************Dark Scanner Job end(spent : %s)*****************************", end - start));
    }
}
