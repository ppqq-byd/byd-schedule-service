package com.ora.blockchain.task.jobs;

import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.task.ScheduledJob;
import com.ora.blockchain.task.Task;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ScheduledJob(name = "darkJob", cronExp = "0 */1 * * * ?")
public class DarkJob implements Job {

    @Autowired
    private Task task;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("dark");
    }
}
