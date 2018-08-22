package com.ora.blockchain.service.blockscanner.impl.ethfamily;


import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.mybatis.entity.block.EthereumBlock;
import com.ora.blockchain.mybatis.entity.eth.EthereumERC20;
import com.ora.blockchain.mybatis.entity.eth.EthereumScanCursor;
import com.ora.blockchain.mybatis.entity.eth.EthereumTransaction;
import com.ora.blockchain.mybatis.entity.wallet.ERC20Sum;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBalance;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBind;
import com.ora.blockchain.mybatis.mapper.block.EthereumBlockMapper;
import com.ora.blockchain.mybatis.mapper.eth.EthereumScanCursorMapper;
import com.ora.blockchain.mybatis.mapper.transaction.EthereumERC20Mapper;
import com.ora.blockchain.mybatis.mapper.transaction.EthereumTransactionMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBalanceMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBindMapper;
import com.ora.blockchain.service.blockscanner.impl.BlockScanner;
import com.ora.blockchain.service.web3j.Web3;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

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

    @Autowired
    private EthereumScanCursorMapper scanCursorMapper;

    @Autowired
    private WalletAccountBalanceMapper balanceMapper;

    private static final int DEPTH = 12;

    @Override
    public boolean isNeedScanHeightLasted(Long needScanBlock) throws IOException {
        try {
            if(needScanBlock>Web3.getCurrentBlockHeight().longValue())
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
            dbBlockHeight = initBlockHeight;
        }else {
            dbBlockHeight = dbBlockHeight + 1;
        }
        return dbBlockHeight;
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
        if(address==null)return false;
        for(EthereumERC20 erc20:list){
            if(erc20.getContractAddress().toLowerCase().equals(address.toLowerCase())){
                return true;
            }
        }

        return false;
    }


    private void recordCursor(Long blockHeight){
        EthereumScanCursor cursor = new EthereumScanCursor();
        cursor.setCurrentBlock(blockHeight);
        cursor.setSyncStatus(0);
        scanCursorMapper.insert(cursor);
    }

    @Override
    public void syncBlockAndTx(Long blockHeight) throws Exception {
        //记录游标
        recordCursor(blockHeight);

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
     * 处理转入 转出逻辑
     * @param address
     * @param isOut
     */
    private void processEthCoinAccount(String address,boolean isOut,EthereumTransaction tx){
        WalletAccountBalance wc =
                balanceMapper.findBalanceByAddressAndCointype(address,Constants.COIN_TYPE_ETH);
        if(wc!=null){

            if(isOut){
                wc.setFrozenBalance(wc.getFrozenBalance()-tx.getValue());//冻结减去值
                wc.setTotalBalance(wc.getTotalBalance()-tx.getValue());//真正的余额减去值
                //TODO 找到address 对应的以太坊的账户 减去 tx.gasUsed
            }else{
                wc.setTotalBalance(wc.getTotalBalance()+tx.getValue());//真正的余额加上值
            }

            balanceMapper.update(wc);
        }

    }

    private void processToken(EthereumTransaction tx,boolean out){
        WalletAccountBalance account =
                this.balanceMapper.findBalanceByContractAddressAndCoinType("coin_eth",
                        Constants.COIN_TYPE_ETH,tx.getContractAddress(),out==true?tx.getFrom():tx.getTo());

        Long cost = 0L;
        if(out){
            cost = tx.getValue()+tx.getGasUsed();
            account.setTotalBalance(account.getTotalBalance()-cost);
            account.setFrozenBalance(account.getFrozenBalance()-cost);
        }else {
            cost = tx.getValue();
        }

        balanceMapper.update(account);
    }

    @Override
    public void updateAccountBalanceByConfirmTx(Long lastedBlock) {
        EthereumScanCursor cursor =
                this.scanCursorMapper.getEthereumNotConfirmScanCursor("coin_eth");
        if(cursor==null){
            return;
        }

        if(lastedBlock - cursor.getCurrentBlock()>=DEPTH){//已经被12个块确认 需要处理
            //处理没被处理过的交易
            List<EthereumTransaction> txList =
                    this.txMapper.queryTxByBlockNumber("coin_eth",cursor.getCurrentBlock());

            Set<String> accounts = new HashSet<>();

            for(EthereumTransaction tx:txList){
                if(tx.getContractAddress()!=null){//处理token的逻辑
                    //转出token
                    processToken(tx,true);
                    //转入token
                    processToken(tx,false);
                }else{//eth币的逻辑
                    //处理转出的逻辑
                    processEthCoinAccount(tx.getFrom(),true,tx);
                    //处理转入的逻辑
                    processEthCoinAccount(tx.getTo(),false,tx);
                }

                accounts.add(tx.getFrom());
                accounts.add(tx.getTo());
            }

            //将处理过的状态设为1
            cursor.setSyncStatus(1);
            scanCursorMapper.update(cursor);

            //TODO 这里什么时候 怎么做检查
            checkAccountBalance(accounts);
        }

    }

    /**
     * 检查账户是否同步正确
     * @param accounts
     */
    private void checkAccountBalance(Set<String> accounts){
        for (String address:accounts){
            //先检查Token 然后累计gas_used
            List<WalletAccountBalance> list =
                    this.balanceMapper.findTokenBalanceByAddressAndCointype(address,Constants.COIN_TYPE_ETH);
            Long gasUsed = 0L;
            for(WalletAccountBalance wab:list){
                //根据地址+token id 检查tx中是否与 balance记录的一致
                ERC20Sum result = balanceMapper.findERC20OutSumByAddressAndTokenId(address,wab.getTokenId());

                Long in = balanceMapper.findERC20InSumByAddressAndTokenId(address,wab.getTokenId());

                if(wab.getTotalBalance()!=in - result.getSumValue()){
                    //TODO
                    log.error("error;tokenBalance;coin:eth;address:"+address+";");
                    wab.setTotalBalance(in-result.getSumValue());
                    balanceMapper.update(wab);
                }

                gasUsed = gasUsed + result.getGasUsed();
            }

            //再检查以太坊的余额
            WalletAccountBalance ethAccountBalance =
                    balanceMapper.findBalanceByAddressAndCointype(address,Constants.COIN_TYPE_ETH);
        }
    }

    @Override
    public Long getLastedBlock(String coinType) {
        Long lastedBlock = this.blockMapper.queryMaxBlockInDb(coinType);
        return lastedBlock;
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
     * 根据地址过滤tx
     * @param ethAccounts
     * @param tx
     * @return
     */
    private boolean filterByAddress(List<WalletAccountBind> ethAccounts,EthBlock.TransactionObject tx){
        for(WalletAccountBind account:ethAccounts){
            //如果是平台账户的地址或者合约的地址
            if(account.getAddress().equals(tx.getFrom())||
                    account.getAddress().equals(tx.getTo())){
                return true;
            }

        }

        return false;
    }

    /**
     * 将不属于平台账户的交易 过滤掉
     * @param block
     * @return
     */
    private List<EthereumTransaction> filterTx(EthBlock block,List<EthereumERC20> erc20) throws Exception {

        List<EthereumTransaction> needAddList = new ArrayList<>();

        List<EthBlock.TransactionResult> results = block.getBlock().getTransactions();
        //非合约的账户
        List<WalletAccountBind> notContractAccounts = getEthAccounts(results);


            for(EthBlock.TransactionResult r:results)
            {
                EthBlock.TransactionObject tx = (EthBlock.TransactionObject) r.get();
                EthereumTransaction dbTx = new EthereumTransaction();
                if(tx.getTo()==null){
                    log.info("is not erc20 contract:"+tx.getHash());
                }
                if(isContract(erc20,tx.getTo())){//如果是ERC20
                    dbTx.transEthTransaction(tx);
                    dbTx.setContractAddress(tx.getTo());
                    String result[] = this.processInput(tx.getInput().toLowerCase());

                    //如果从inputData解析出的账户属于ERC20或from属于ERC20
                    dbTx.transEthTransaction(tx);
                    dbTx.setTo(result[0]);
                    dbTx.setValue(Long.parseLong(result[1]));
                    Set<String> address = new HashSet<>();
                    address.add(dbTx.getFrom());
                    address.add(dbTx.getTo());
                    List<WalletAccountBind> accoutns = accountBindMapper.queryWalletByAddress(address);
                    if(accoutns.size()>0){
                        needAddList.add(dbTx);
                    }

                }else {

                    //如果是平台账户的地址
                    if(filterByAddress(notContractAccounts,tx)){
                        dbTx.transEthTransaction(tx);
                        needAddList.add(dbTx);
                    }


                }

            }


        return needAddList;
    }

}
