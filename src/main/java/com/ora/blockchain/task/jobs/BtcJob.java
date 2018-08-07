package com.ora.blockchain.task.jobs;

import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.rpc.IRpcService;
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
@ScheduledJob(name = "BtcJob", cronExp = "* */10 * * * ?")
@DisallowConcurrentExecution
public class BtcJob implements Job {
    @Resource
    @Qualifier("btcRpcServiceImpl")
    private IRpcService btcRpcService;
    @Resource
    @Qualifier("btcBlockServiceImpl")
    private IBlockService btcBlockService;
    @Autowired
    private Task task;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("********************Btc Job start......************************");
        long start = System.currentTimeMillis();
        task.task(Constants.COIN_TYPE_BTC, btcBlockService, btcRpcService);
        long end = System.currentTimeMillis();
        System.out.println(String.format("*********************Btc Job end(spent : %s)*****************************", end - start));
    }
}
