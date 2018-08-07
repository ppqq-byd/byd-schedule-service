package com.ora.blockchain.task.jobs;

import com.ora.blockchain.task.ScheduledJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
@ScheduledJob(name = "ethJob", cronExp = "10 */1 * * * ?")
public class EthereumJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }
}
