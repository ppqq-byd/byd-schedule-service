package com.ora.blockchain.task.jobs;

import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.rpc.IRpcService;
import com.ora.blockchain.task.ScheduledJob;
import com.ora.blockchain.task.Task;
import org.apache.tomcat.util.bcel.Const;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@ScheduledJob(name = "darkJob", cronExp = "0 */1 * * * ?")
@DisallowConcurrentExecution
public class DarkJob implements Job {
    @Resource
    @Qualifier("darkRpcServiceImpl")
    private IRpcService darkRpcService;
    @Resource
    @Qualifier("darkBlockServiceImpl")
    private IBlockService darkBlockService;
    @Autowired
    private Task task;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("********************Dark Job start......************************");
        long start = System.currentTimeMillis();
//        task.task(Constants.COIN_TYPE_DARK, darkBlockService, darkRpcService);
        long end = System.currentTimeMillis();
        System.out.println(String.format("*********************Dark Job end(spent : %s)*****************************", end - start));
    }
}
