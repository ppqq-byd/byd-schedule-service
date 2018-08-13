package com.ora.blockchain.task.jobs;

import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.blockscanner.IBlockScanner;
import com.ora.blockchain.task.ScheduledJob;
import com.ora.blockchain.task.Task;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@DisallowConcurrentExecution
@ScheduledJob(name = "ethJob", cronExp = "10 */1 * * * ?")
public class EthereumJob implements Job {

    @Resource
    @Qualifier("ethBlockScaner")
    private IBlockScanner ethBlockScanner;

    @Autowired
    private Task task;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("********************Eth Job start......************************");
        long start = System.currentTimeMillis();

        long end = System.currentTimeMillis();
        System.out.println(String.format("*********************Eth Job end(spent : %s)*****************************", end - start));

    }
}
