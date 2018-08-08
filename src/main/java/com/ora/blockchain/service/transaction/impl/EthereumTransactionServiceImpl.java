package com.ora.blockchain.service.transaction.impl;


import com.ora.blockchain.mybatis.entity.block.EthereumBlock;
import com.ora.blockchain.mybatis.entity.transaction.EthereumTransaction;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBind;
import com.ora.blockchain.mybatis.mapper.block.EthereumBlockMapper;
import com.ora.blockchain.mybatis.mapper.transaction.EthereumTransactionMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBindMapper;
import com.ora.blockchain.service.transaction.IEthereumTransactionService;
import com.ora.blockchain.service.web3j.Web3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EthereumTransactionServiceImpl implements IEthereumTransactionService {

    @Autowired
    private EthereumTransactionMapper txMapper;

    @Autowired
    private EthereumBlockMapper blockMapper;

    @Autowired
    private WalletAccountBindMapper accountBindMapper;

    private static final int DEPTH = 12;

    private static final int COIN_TYPE_ETH = 2;

    private long getSyncNumber( long needSync){
        if(needSync>DEPTH){
            return DEPTH;
        }else{
            return needSync;
        }
    }

    @Override
    @Transactional
    public void inserNewBlock(Long initBlockNumber) {

        try {
            long dbBlockHeight = blockMapper.queryMaxBlockInDb("coin_eth");
            if(dbBlockHeight==0){
                dbBlockHeight = initBlockNumber;
            }
            long needSync = Web3.getCurrentBlockHeight().longValue()-dbBlockHeight;
            if(needSync>0){
                needSync = getSyncNumber(needSync);

                List<EthereumBlock> list = new ArrayList<>();
                List<EthereumTransaction> txList = new ArrayList<>();

                for(int i=1;i<=needSync;i++){
                    EthBlock block = Web3.getBlockInfoByNumber(dbBlockHeight+i);
                    EthereumBlock newBlock = new EthereumBlock();
                    newBlock.trans(block);
                    list.add(newBlock);

                    addTx(block,txList);
                }

                blockMapper.insertBlockList("coin_eth",list);
                txList = filterTx(txList);
                if(txList.size()!=0){
                    txMapper.insertTxList("coin_eth",txList);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 将不属于平台账户的交易 过滤掉
     * @param list
     */
    private List<EthereumTransaction> filterTx( List<EthereumTransaction> list){
        List<WalletAccountBind> ethAccounts = accountBindMapper.queryWalletAccountBindByCoinType(COIN_TYPE_ETH);

        Iterator<EthereumTransaction> it = list.iterator();
        List<EthereumTransaction> needAddList = new ArrayList<>();
        if(ethAccounts==null)return needAddList;
        while (it.hasNext()){
            EthereumTransaction tx = it.next();
            for(WalletAccountBind account:ethAccounts){
                if(account.getAddress().equals(tx.getFrom())||
                        account.getAddress().equals(tx.getTo())){
                    needAddList.add(tx);
                    break;
                }
            }
        }

        return needAddList;
    }

    private void addTx(EthBlock ethBlock,List<EthereumTransaction> txList){

        List<EthBlock.TransactionResult> list = ethBlock.getBlock().getTransactions();
        for(EthBlock.TransactionResult r:list){
            EthBlock.TransactionObject o = (EthBlock.TransactionObject) r.get();
            EthereumTransaction tx = new EthereumTransaction();
            tx.transEthTransaction(o);
            txList.add(tx);
        }

    }

    @Override
    @Transactional
    public void confirmBlock(){

        try {
            long blockHeight = blockMapper.queryMaxConfirmBlockInDb("coin_eth");
            if(blockHeight==0){
                blockHeight = blockMapper.queryMinBlockInDb("coin_eth");
            }

            //检查链的状态 如果节点上，当前区块的12个子区块都确认了这个区块 说明可以和数据库同步
            EthBlock[] chainBlocks = verifyChainBlock(blockHeight);
            if(chainBlocks!=null){//如果当前geth节点上 该区块向前12块都是能确认的 比对数据库的状态
                List<EthereumBlock> list = blockMapper.queryPreEthBlocks("coin_eth",
                        blockHeight,
                        blockHeight+DEPTH-1);

                updateDbDataWithChain(chainBlocks,list);
            }


        }catch (Exception e){

        }


    }


    private void updateDbDataWithChain(EthBlock[] chainBlocks, List<EthereumBlock> list){
        List<EthereumBlock> sortedList =
                list.stream().sorted(Comparator.comparing(EthereumBlock::getBlockNumber)).collect(Collectors.toList());

        List<String> hashList = new ArrayList<>();

        for(int i = 0;i<sortedList.size();i++){
            EthereumBlock dbBlock = sortedList.get(i);
            EthBlock.Block chainBlock = chainBlocks[i].getBlock();
            if(compareDbBlockWithChain(dbBlock,chainBlock)){//如果相同 直接更新是否确认字段
                //update db confirm status
                hashList.add(chainBlock.getHash());
            }else {
                //update db record and status
                EthereumBlock newDbBlock =new EthereumBlock();
                newDbBlock.trans(chainBlocks[i]);
                newDbBlock.setConfirmNumber(1);
                blockMapper.updateByBlockNumber(newDbBlock);
            }
        }

        if(hashList.size()>0){
            blockMapper.updateSetConfirmStatusByHash("coin_eth",1,hashList);
        }
    }

    //查看数据库中哪些块与链上的块不同

    /**
     *
     * @param dbBlock
     * @param chainBlock
     * @return
     */
    private boolean compareDbBlockWithChain(EthereumBlock dbBlock,EthBlock.Block chainBlock ){
        if(!dbBlock.getHash().equals(chainBlock.getHash())){
            return false;
        }

        if(!dbBlock.getParentHash().equals(chainBlock.getParentHash())){
            return false;
        }

        //以太坊的时间戳是以秒为单位
        if(dbBlock.getBlockTime().getTime()!=(chainBlock.getTimestamp().longValue()*1000L)){
            return false;
        }

        if(dbBlock.getDifficulty()!=chainBlock.getDifficulty().longValue()){
            return false;
        }

        return true;
    }

    /**
     * 检查链上的状态
     * @param blockHeight
     * @return
     * @throws Exception
     */
    private EthBlock[] verifyChainBlock(long blockHeight)
            throws Exception {
        //当前节点的最高高度
        Long lastedBlockNumber = Web3.getCurrentBlockHeight().longValue();
        if(lastedBlockNumber - blockHeight < DEPTH){//如果节点没有确认12个 啥都不干
            return  null;
        }
        EthBlock[] blocks = new EthBlock[DEPTH];
        for(int i=0;i<blocks.length;i++){//从blockHeight这个区块高度向后找12个区块
            EthBlock block = Web3.getBlockInfoByNumber(blockHeight+i);
            blocks[i] = block;
        }

        boolean flg = true;
        for(int s = blocks.length-1;s>0;s--){
            //当前区块的父区块与上一个区块的hash值不同
            if( !blocks[s].getBlock().getParentHash().
                                    equals(blocks[s-1].getBlock().getHash())){
                                flg = false;
                                break;
             }

        }
        if(!flg){
            return null;
        }
        return blocks;
    }
}
