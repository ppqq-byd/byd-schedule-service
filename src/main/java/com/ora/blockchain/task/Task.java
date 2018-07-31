package com.ora.blockchain.task;

import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.mybatis.entity.block.Block;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.rpc.IRpcService;
import com.ora.blockchain.utils.BlockchainUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class Task {

    public static final int BLOCK_DEPTH = 6;

    @Resource
    @Qualifier("darkRpcServiceImpl")
    private IRpcService darkRpcService;
    @Resource
    @Qualifier("darkBlockServiceImpl")
    private IBlockService darkBlockService;

    @Resource
    @Qualifier("ltcRpcServiceImpl")
    private IRpcService ltcRpcService;
    @Resource
    @Qualifier("ltcBlockServiceImpl")
    private IBlockService ltcBlockService;

    public void task(String database,IBlockService blockService,IRpcService rpcService) {
        List<Block> dbBlockList = blockService.queryBlockList(Constants.COIN_TYPE_DARK, null,6);
        if (null == dbBlockList || dbBlockList.isEmpty()) {
            List<Block> blockList = rpcService.getPreviousBlockList(BLOCK_DEPTH, null);
            for (Block block : blockList) {
                blockService.insertBlock(Constants.COIN_TYPE_DARK, block);
            }
        }else{
            List<Block> blockList = rpcService.getPreviousBlockList(BLOCK_DEPTH,null);
            blockService.updateBlock(Constants.COIN_TYPE_DARK,dbBlockList,blockList);
            while(true){
                blockList = rpcService.getPreviousBlockList(1,blockList.get(blockList.size()-1).getPreviousBlockHash());
                dbBlockList = blockService.queryBlockList(Constants.COIN_TYPE_DARK,blockList.get(0).getHeight(),1);
                if(BlockchainUtil.isEqualCollection(blockList,dbBlockList)){
                    break;
                }
                blockService.updateBlock(Constants.COIN_TYPE_DARK,dbBlockList,blockList);
            }
        }
    }

//    @Scheduled(fixedRate = 2 * 60 * 1000)
    public void darkTask() {
        task(Constants.COIN_TYPE_DARK,darkBlockService,darkRpcService);
    }
//    @Scheduled(fixedRate = 2 * 60 * 1000)
    public void ltcTask(){
        task(Constants.COIN_TYPE_LTC,ltcBlockService,ltcRpcService);
    }
}
