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
@ScheduledJob(name = "btgAccountJob", cronExp = "*/30 * * * * ?")
@DisallowConcurrentExecution
public class BtgAccountJob implements Job {
    @Resource
    @Qualifier("btgBlockScanner")
    private IBlockScanner scanner;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("********************Btg Account Job start......************************");
        long start = System.currentTimeMillis();
        try {
//            scanner.updateAccount(CoinType.BTG.name());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Btg Account JOb error! " + e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info(String.format("*********************Btg Account Job end(spent : %s)*****************************", end - start));
    }
}
