package com.ora.blockchain.service.blockscanner.impl.ethfamily;


import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.mybatis.entity.block.EthereumBlock;
import com.ora.blockchain.mybatis.entity.output.Output;
import com.ora.blockchain.mybatis.entity.transaction.EthereumERC20;
import com.ora.blockchain.mybatis.entity.transaction.EthereumTransaction;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBind;
import com.ora.blockchain.mybatis.mapper.block.EthereumBlockMapper;
import com.ora.blockchain.mybatis.mapper.transaction.EthereumERC20Mapper;
import com.ora.blockchain.mybatis.mapper.transaction.EthereumTransactionMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBindMapper;
import com.ora.blockchain.service.blockscanner.impl.BlockScanner;
import com.ora.blockchain.service.web3j.Web3;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Int;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
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

    @Autowired
    private EthereumERC20Mapper erc20Mapper;

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

    /**
     * 处理tx的状态 将集合分为 待update 和 待 insert
     * @param filteredTx
     * @param inDbTx
     * @return
     */
    private  Map<String, List<EthereumTransaction> > processTxStatus(List<EthereumTransaction> filteredTx,
                                                  List<EthereumTransaction> inDbTx){

        List<EthereumTransaction> needUpdateTx = new ArrayList<>();

        List<EthereumTransaction> needInsertTx = null;

        Iterator<EthereumTransaction> it = filteredTx.iterator();
        while (it.hasNext()){
            EthereumTransaction tx = it.next();
            tx.setStatus(Constants.TXSTATUS_CONFIRMING);
            tx.setIsDelete(0);
            boolean isDelete = false;
            for(EthereumTransaction dbTx:inDbTx){
                if(dbTx.getTxId().equals(tx.getTxId())){
                    needUpdateTx.add(tx);
                    isDelete = true;
                    break;
                }
            }
            if(isDelete){
                it.remove();
            }
        }

        needInsertTx = filteredTx;
        Map<String, List<EthereumTransaction> > map = new HashMap<>();
        map.put("insert",needInsertTx);
        map.put("update",needUpdateTx);

        return map;
    }

    /**
     * 判断是否是合约
     * @param list
     * @param address
     * @return
     */
    private boolean isContract(List<EthereumERC20> list,String address){
        for(EthereumERC20 erc20:list){
            if(erc20.getContractAddress().toLowerCase().equals(address.toLowerCase())){
                return true;
            }
        }

        return false;
    }



    @Override
    public void syncBlockAndTx(Long blockHeight) throws Exception {

        EthBlock block = Web3.getBlockInfoByNumber(blockHeight);
        EthereumBlock dbBlock = new EthereumBlock();
        dbBlock.trans(block);
        blockMapper.insertBlock("coin_eth",dbBlock);

        //获取erc20的定义
        List<EthereumERC20> erc20 = erc20Mapper.queryERC20("coin_eth");

        //过滤掉非系统账户的tx 以及 非支持的ERC20的tx
        List<EthereumTransaction> filteredTx = filterTx(block,erc20);
        System.out.println("filteredTx:"+filteredTx.size());

        if(filteredTx==null||filteredTx.size()==0)return;
        //找出已在DB中存在的tx
        List<EthereumTransaction> inDbTx = txMapper.queryTxInDb("coin_eth",filteredTx);
        //根据inDBtx集合 将filteredTx处理为 待update和待insert两个集合
        Map<String, List<EthereumTransaction> > map = processTxStatus(filteredTx,inDbTx);

        if(map.get("update")!=null&&map.get("update").size()>0){

            txMapper.batchUpdate("coin_eth",
                    map.get("update"));
        }

        if(map.get("insert")!=null&&map.get("insert").size()>0){

            txMapper.insertTxList("coin_eth",map.get("insert"));
        }

    }


    /**
     * 获取和平台相关的账户
     * @param results
     * @return
     */
    private List<WalletAccountBind> getEthAccounts( List<EthBlock.TransactionResult> results){
        HashSet<String> address = new HashSet<String>();
        for(int i=0;i<results.size();i++)
        {
            EthBlock.TransactionObject tx = (EthBlock.TransactionObject) results.get(i);
            address.add(tx.getTo());
            address.add(tx.getFrom());

        }
        List<WalletAccountBind> ethAccounts = accountBindMapper.queryWalletByAddress(address);
        return ethAccounts;
    }

    private String[] processInput(String input){
        if(input.substring(0,10).equals("0xa9059cbb")){
            String toAddress = input.substring(10,74);
            String value = input.substring(74,138);
            System.out.println(toAddress);
            byte[] ss = new BigInteger(toAddress,16).toByteArray();
            toAddress = Hex.toHexString(ss);
            if(toAddress.length()==42&&toAddress.substring(0,2).equals("00")){
                toAddress = toAddress.substring(2,42);
            }

            Long v = new BigInteger(value,16).longValue();

            String result[] = new String[2];
            result[0] = "0x"+toAddress;
            result[1] = String.valueOf(v);
            return result;
        }
        return null;
    }

    /**
     * 将不属于平台账户的交易 过滤掉
     * @param block
     * @return
     */
    private List<EthereumTransaction> filterTx(EthBlock block,List<EthereumERC20> erc20) throws Exception {

        List<EthereumTransaction> needAddList = new ArrayList<>();

        List<EthBlock.TransactionResult> results = block.getBlock().getTransactions();
        List<WalletAccountBind> ethAccounts = getEthAccounts(results);


            for(EthBlock.TransactionResult r:results)
            {
                EthBlock.TransactionObject tx = (EthBlock.TransactionObject) r.get();
                EthereumTransaction dbTx = new EthereumTransaction();

                if(isContract(erc20,tx.getTo())){//如果是ERC20
                    dbTx.transEthTransaction(tx);
                    dbTx.setContractAddress(tx.getTo());
                    String result[] = this.processInput(tx.getInput().toLowerCase());
                    dbTx.setTo(result[0]);
                    dbTx.setValue(Double.parseDouble(result[1]));
                    needAddList.add(dbTx);
                }else {
                    for(WalletAccountBind account:ethAccounts){
                        //如果是平台账户的地址或者合约的地址
                        if(account.getAddress().equals(tx.getFrom())||
                                account.getAddress().equals(tx.getTo())){
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
