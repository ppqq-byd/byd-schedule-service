package com.ora.blockchain.service.blockscanner.impl.ethfamily;


import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.mybatis.entity.block.EthereumBlock;
import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.entity.transaction.EthereumTransaction;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBind;
import com.ora.blockchain.mybatis.mapper.block.EthereumBlockMapper;
import com.ora.blockchain.mybatis.mapper.transaction.EthereumTransactionMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBindMapper;
import com.ora.blockchain.service.blockscanner.impl.BlockScanner;
import com.ora.blockchain.service.web3j.Web3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Int;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service("ethBlockScaner")
@Slf4j
public class EthereumBlockScanner extends BlockScanner {

    @Autowired
    private EthereumTransactionMapper txMapper;

    @Autowired
    private EthereumBlockMapper blockMapper;

    @Autowired
    private WalletAccountBindMapper accountBindMapper;

    private static final int DEPTH = 12;

    @Override
    public boolean isNeedScanHeightLasted(Long needScanBlock) throws IOException {
        try {
            if(needScanBlock>=Web3.getCurrentBlockHeight().longValue())
            return true;
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            throw e;
        }
        return false;
    }

    @Override
    public void deleteBlockAndUpdateTx(Long blockHeight) {

        blockMapper.deleteBlockByBlockNumber("coin_eth",blockHeight);

        txMapper.updateTransacion("coin_eth",blockHeight,null);
    }

    @Override
    public Long getNeedScanBlockHeight(Long initBlockHeight){
        long dbBlockHeight = blockMapper.queryMaxBlockInDb("coin_eth");
        if(dbBlockHeight==0){
            return initBlockHeight;
        }

        return ++dbBlockHeight;
    }

    @Override
    public boolean verifyIsolatedBlock(Long needScanBlock) throws Exception {

        //现有数据库中最后一个块
        EthereumBlock dbBlock = blockMapper.
                queryEthBlockByBlockNumber("coin_eth",(needScanBlock-1));

        //与节点中的对比
        EthBlock block = Web3.getBlockInfoByNumber(needScanBlock-2);


        if(dbBlock!=null&&!dbBlock.getParentHash().equals(block.getBlock().getHash())){
            return true;
        }

        return false;
    }

    @Override
    public void syncBlockAndTx(Long blockHeight) throws Exception {

        EthBlock block = Web3.getBlockInfoByNumber(blockHeight);
        EthereumBlock dbBlock = new EthereumBlock();
        dbBlock.trans(block);
        blockMapper.insertBlock("coin_eth",dbBlock);

        List<EthereumTransaction> sendTx = filterTx(block,false);
        List<EthereumTransaction> receiveTx = filterTx(block,true);
        System.out.println("sendTx:"+sendTx.size()+"/receiveTx:"+receiveTx.size());
        if(sendTx!=null&&sendTx.size()>0){
            //TODO sendTx 中的ERC20重新处理
            txMapper.batchUpdate("coin_eth",
                    sendTx);
        }

        if(receiveTx!=null&&receiveTx.size()>0){
            //TODO receiveTx 中的ERC20重新处理
            txMapper.insertTxList("coin_eth",receiveTx);
        }

    }


    /**
     * 获取和平台相关的账户
     * @param results
     * @param isReceive 收款还是付款
     * @return
     */
    private List<WalletAccountBind> getEthAccounts( List<EthBlock.TransactionResult> results,
                                                    boolean isReceive){
        HashSet<String> address = new HashSet<String>();
        for(int i=0;i<results.size();i++)
        {
            EthBlock.TransactionObject tx = (EthBlock.TransactionObject) results.get(i);
            if(isReceive){
                address.add(tx.getTo());
            }else {
                address.add(tx.getFrom());
            }

        }
        List<WalletAccountBind> ethAccounts = accountBindMapper.queryWalletByAddress(address);
        return ethAccounts;
    }

    /**
     * 将不属于平台账户的交易 过滤掉
     * @param block
     * @return
     */
    private List<EthereumTransaction> filterTx(EthBlock block,boolean isReceive){

        List<EthereumTransaction> needAddList = new ArrayList<>();

        List<EthBlock.TransactionResult> results = block.getBlock().getTransactions();
        List<WalletAccountBind> ethAccounts = getEthAccounts(results,isReceive);

        if(ethAccounts!=null&&ethAccounts.size()>0){
            for(EthBlock.TransactionResult r:results)
            {
                EthBlock.TransactionObject tx = (EthBlock.TransactionObject) r.get();
                for(WalletAccountBind account:ethAccounts){
                    if(account.getAddress().equals(tx.getFrom())||
                            account.getAddress().equals(tx.getTo())){
                        EthereumTransaction dbTx = new EthereumTransaction();
                        dbTx.transEthTransaction(tx);
                        needAddList.add(dbTx);
                        break;
                    }
                }
            }
        }

        return needAddList;
    }

}
