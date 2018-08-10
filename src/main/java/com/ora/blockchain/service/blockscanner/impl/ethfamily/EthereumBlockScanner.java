package com.ora.blockchain.service.blockscanner.impl.ethfamily;


import com.ora.blockchain.mybatis.entity.block.EthereumBlock;
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
import org.web3j.protocol.core.methods.response.EthBlock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

        return dbBlockHeight++;
    }

    @Override
    public boolean verifyIsolatedBlock(Long needScanBlock) throws Exception {
        EthBlock block = Web3.getBlockInfoByNumber(needScanBlock);
        EthereumBlock dbBlock = blockMapper.
                queryEthBlockByBlockNumber("coin_eth",(needScanBlock-1));
        if(!dbBlock.getHash().equals(block.getBlock().getParentHash())){
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

        List<EthereumTransaction> dbTxList = filterTx(block);
        //delete dbTxList中 txhashid 相同 并且 block_hash为 null的 tx
        clearTxOfIsolatedBlock(dbTxList);
        txMapper.insertTxList("coin_eth",dbTxList);
    }

    private  void clearTxOfIsolatedBlock(List<EthereumTransaction> needInsertTxList){
        List<EthereumTransaction> txOfIsolatedBlock = txMapper.queryIsolatedBlockTx("coin_eth");

        List<String> needDelete = new ArrayList<>();
        Iterator<EthereumTransaction> it =txOfIsolatedBlock.iterator();
        while (it.hasNext()){
            EthereumTransaction txIsolated = it.next();

            for(EthereumTransaction needInsert:needInsertTxList){
                if(needInsert.getTxId().equals(txIsolated.getTxId())){
                    needDelete.add(txIsolated.getTxId());
                    break;
                }
            }

        }

        txMapper.deleteTxByTxhash("coin_eth",needDelete);

    }

    /**
     * 将不属于平台账户的交易 过滤掉
     * @param block
     * @return
     */
    private List<EthereumTransaction> filterTx(EthBlock block){
        List<WalletAccountBind> ethAccounts = accountBindMapper.queryWalletAccountBindByCoinType(2);

        List<EthereumTransaction> needAddList = new ArrayList<>();
        if(ethAccounts==null)return needAddList;

        List<EthBlock.TransactionResult> results = block.getBlock().getTransactions();

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

        return needAddList;
    }

}
