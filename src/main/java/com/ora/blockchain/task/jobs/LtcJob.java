package com.ora.blockchain.task.jobs;

import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.rpc.IRpcService;
import com.ora.blockchain.task.ScheduledJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@ScheduledJob(name = "LtcJob", cronExp = "10 */1 * * * ?")
@DisallowConcurrentExecution
public class LtcJob implements Job {
    @Resource
    @Qualifier("ltcRpcServiceImpl")
    private IRpcService ltcRpcService;
    @Resource
    @Qualifier("ltcBlockServiceImpl")
    private IBlockService ltcBlockService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("********************LTC Job start......************************");
        long start = System.currentTimeMillis();
//        task.task(Constants.COIN_TYPE_LTC, ltcBlockService, ltcRpcService);
        long end = System.currentTimeMillis();
        System.out.println(String.format("*********************LTC Job end(spent : %s)*****************************", end - start));
    }
}
