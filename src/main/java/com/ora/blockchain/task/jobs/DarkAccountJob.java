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
@ScheduledJob(name = "darkAccountJob", cronExp = "*/30 * * * * ?")
@DisallowConcurrentExecution
public class DarkAccountJob implements Job {
    @Resource
    @Qualifier("darkBlockScanner")
    private IBlockScanner scanner;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("********************Dark Account Job start......************************");
        long start = System.currentTimeMillis();
        try {
//            scanner.updateAccount(CoinType.DARK.name());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Dark Account JOb error! " + e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info(String.format("*********************Dark Account Job end(spent : %s)*****************************", end - start));
    }
}
