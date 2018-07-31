package com.ora.blockchain.task.jobs;

import com.ora.blockchain.task.ScheduledJob;
import com.ora.blockchain.task.Task;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ScheduledJob(name = "LtcJob", cronExp = "10 */1 * * * ?")
public class LtcJob implements Job {
    @Autowired
    private Task task;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("ltc");
    }
}
