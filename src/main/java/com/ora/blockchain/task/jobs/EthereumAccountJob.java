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

@Component
@DisallowConcurrentExecution
@ScheduledJob(name = "ethAccountJob", cronExp = "0/5 * * * * ?")
@Slf4j
public class EthereumAccountJob implements Job {

    @Resource
    @Qualifier("ethBlockScaner")
    private IBlockScanner ethBlockScanner;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("********************Eth Account Job start......************************");
        long start = System.currentTimeMillis();
        try {
            ethBlockScanner.updateAccount(CoinType.ETH.name());
        } catch (Exception e) {
            e.printStackTrace();
           log.error("Eth Account job failed:"+e.getMessage(),e);
        }
        long end = System.currentTimeMillis();
        System.out.println(String.format("*********************Eth Account Job end(spent : %s)*****************************", end - start));
    }
}
