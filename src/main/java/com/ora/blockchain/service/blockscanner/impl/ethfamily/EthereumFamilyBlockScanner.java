package com.ora.blockchain.service.blockscanner.impl.ethfamily;


import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.constants.TxStatus;
import com.ora.blockchain.mybatis.entity.block.EthereumBlock;
import com.ora.blockchain.mybatis.entity.eth.EthereumERC20;
import com.ora.blockchain.mybatis.entity.eth.EthereumTransaction;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBalance;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBind;
import com.ora.blockchain.mybatis.mapper.block.EthereumBlockMapper;
import com.ora.blockchain.mybatis.mapper.transaction.EthereumERC20Mapper;
import com.ora.blockchain.mybatis.mapper.transaction.EthereumTransactionMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBalanceMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBindMapper;
import com.ora.blockchain.service.blockscanner.impl.BlockScanner;
import com.ora.blockchain.service.web3j.Web3;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@Service("ethBlockScaner")
@Slf4j
public abstract class EthereumFamilyBlockScanner extends BlockScanner {

    @Autowired
    private EthereumTransactionMapper txMapper;

    @Autowired
    private EthereumBlockMapper blockMapper;

    @Autowired
    private WalletAccountBindMapper accountBindMapper;

    @Autowired
    private EthereumERC20Mapper erc20Mapper;

    @Autowired
    private WalletAccountBalanceMapper balanceMapper;

    private static final int DEPTH = 5;

    protected abstract String getCoinType();

    @Override
    public boolean isNeedScanHeightLasted(Long needScanBlock) throws IOException {
        try {
            if(needScanBlock>Web3.getInstance(getCoinType()).getCurrentBlockHeight().longValue())
            return true;
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            throw e;
        }
        return false;
    }

    @Override
    public void deleteBlockAndUpdateTx(Long blockHeight) {

        blockMapper.deleteBlockByBlockNumber(CoinType.getDatabase(getCoinType()),blockHeight);

        txMapper.updateTransacionIsolate(CoinType.getDatabase(getCoinType()),
                blockHeight,null,
                TxStatus.ISOLATED.ordinal());
    }

    @Override
    public Long getNeedScanBlockHeight(Long initBlockHeight){
        long dbBlockHeight = blockMapper.queryMaxBlockInDb(CoinType.getDatabase(getCoinType()));
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
                queryEthBlockByBlockNumber(CoinType.getDatabase(getCoinType()),(needScanBlock-1));

        //与节点中的对比
        EthBlock block = Web3.getInstance(getCoinType()).getBlockInfoByNumber(needScanBlock-2);


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
            tx.setStatus(TxStatus.CONFIRMING.ordinal());
            tx.setIsDelete(0);
            tx.setIsSender(0);
            boolean isDelete = false;
            for(EthereumTransaction dbTx:inDbTx){
                if(dbTx.getTxId().equals(tx.getTxId())){
                    tx.setIsSender(1);//如果是需要更新的tx 说明数据库已记录 则是提币的tx
                    if(tx.getStatus()==TxStatus.ISOLATED.ordinal()){//如果是孤立的 则处理成孤立确认
                        tx.setStatus(TxStatus.ISOLATEDCONRIMING.ordinal());
                    }
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

    @Override
    public void syncBlockAndTx(Long blockHeight) throws Exception {

        EthBlock block = Web3.getInstance(getCoinType()).getBlockInfoByNumber(blockHeight);
        EthereumBlock dbBlock = new EthereumBlock();
        dbBlock.trans(block);
        blockMapper.insertBlock(CoinType.getDatabase(getCoinType()),dbBlock);

        //获取erc20的定义
        List<EthereumERC20> erc20 = erc20Mapper.queryERC20(CoinType.getDatabase(getCoinType()));

        //过滤掉非系统账户的tx 以及 非支持的ERC20的tx
        List<EthereumTransaction> filteredTx = filterTx(block,erc20);
        System.out.println("filteredTx:"+filteredTx.size());

        if(filteredTx==null||filteredTx.size()==0)return;
        //找出已在DB中存在的tx
        List<EthereumTransaction> inDbTx = txMapper.queryTxInDb(CoinType.getDatabase(getCoinType()),filteredTx);
        //根据inDBtx集合 将filteredTx处理为 待update和待insert两个集合
        Map<String, List<EthereumTransaction> > map = processTxStatus(filteredTx,inDbTx);

        if(map.get("update")!=null&&map.get("update").size()>0){

            txMapper.batchUpdate(CoinType.getDatabase(getCoinType()),
                    map.get("update"));
        }

        if(map.get("insert")!=null&&map.get("insert").size()>0){

            txMapper.insertTxList(CoinType.getDatabase(getCoinType()),map.get("insert"));
        }

    }


    /**
     * 处理转入 转出逻辑
     * @param tx
     */
    private void processEthCoinAccount(EthereumTransaction tx){
        //如果是提币
        if(tx.getIsSender()==1){
            WalletAccountBalance out =
                    balanceMapper.findBalanceOfCoinByAddressAndCointype(tx.getFrom(),getCoinType());

            //内转内
            WalletAccountBalance in =
                    balanceMapper.findBalanceOfCoinByAddressAndCointype(tx.getTo(),getCoinType());
            if(in!=null){
                in.setTotalBalance(in.getTotalBalance().add(tx.getValue()));
                balanceMapper.update(in);
            }

            //冻结减去gasLimit*gasPrice
            out.setFrozenBalance(out.getFrozenBalance().
                    subtract(tx.getGasPrice().multiply(tx.getGasLimit())));

            //账户减去交易的amount再减去gasUsed
            out.setTotalBalance(out.getTotalBalance().subtract(tx.getValue()).subtract(
                    tx.getGasUsed()
            ));

            balanceMapper.update(out);
        }else{//如果是收币

            WalletAccountBalance in =
                    balanceMapper.findBalanceOfCoinByAddressAndCointype(tx.getFrom(),getCoinType());

            in.setTotalBalance(in.getTotalBalance().add(tx.getValue()));

            balanceMapper.update(in);
        }

    }

    /**
     * 处理ERC20代币
     * @param tx
     * @return
     */
    private void processToken(EthereumTransaction tx){
        boolean out = true;
        if(tx.getIsSender()==0){
            out = false;
        }
        WalletAccountBalance account =
                this.balanceMapper.findBalanceByContractAddressAndCoinType(CoinType.getDatabase(getCoinType()),
                        getCoinType(),tx.getContractAddress(),out==true?tx.getFrom():tx.getTo());

        //转出分为内转外和内转内
        if(out){
                WalletAccountBalance receiveAccount =
                        this.balanceMapper.findBalanceByContractAddressAndCoinType(CoinType.getDatabase(getCoinType()),
                                getCoinType(),tx.getContractAddress(),tx.getTo());

                //内转内
                if(receiveAccount!=null){
                    //内部收款账户收钱
                    receiveAccount.setTotalBalance(
                            receiveAccount.getTotalBalance().add(tx.getValue()));
                    balanceMapper.update(receiveAccount);
                }

                //这里处理代币的账户减钱
                account.setTotalBalance(account.getTotalBalance().subtract(tx.getValue()));
                account.setFrozenBalance(account.getFrozenBalance().subtract(tx.getValue()));

                //这里处理代币所属账户的ETH账户更新gas费用
                WalletAccountBalance ethAccount = balanceMapper.findBalanceOfCoinByAddressAndCointype(tx.getFrom(),
                            getCoinType());

                //冻结的手续费为gasLimit*gasPrice
                ethAccount.setFrozenBalance(ethAccount.getFrozenBalance().
                        subtract(tx.getGasLimit().multiply(tx.getGasPrice())));
                //账户的手续费为gasUsed
                ethAccount.setTotalBalance(ethAccount.getTotalBalance().subtract(tx.getGasUsed()));
                balanceMapper.update(ethAccount);
            }else {
                account.setTotalBalance(account.getTotalBalance().add(tx.getValue()));
            }

            balanceMapper.update(account);

    }

    private void setAccountsMap(HashMap<String,HashSet<String>> tokenAccounts,
                                EthereumTransaction tx, String ethAccountAddress,boolean isContract){

        HashSet<String> addresses = tokenAccounts.get(ethAccountAddress);
        addresses = addresses!=null?addresses:new HashSet<String>();
        if(isContract){
            addresses.add(tx.getContractAddress());
        }else{
            addresses.add(getCoinType());
        }

        tokenAccounts.put(ethAccountAddress,addresses);

    }

    private void processConrimingTx(Long lastedBlock){
        List<EthereumTransaction> txList = this.txMapper.queryTxByStatus(CoinType.getDatabase(getCoinType()),TxStatus.CONFIRMING.ordinal());
        List<EthereumTransaction> isolatedTxList = this.txMapper.queryTxByStatus(CoinType.getDatabase(getCoinType()),TxStatus.ISOLATEDCONRIMING.ordinal());
        txList.addAll(isolatedTxList);

        for(EthereumTransaction tx:txList){
            if(lastedBlock - tx.getBlockHeight()>=DEPTH){
                if(!StringUtils.isEmpty(tx.getContractAddress())){//处理token的逻辑
                    //token账户
                    processToken(tx);
                }else{//eth币的处理逻辑
                    processEthCoinAccount(tx);
                }
                tx.setStatus(TxStatus.COMPLETE.ordinal());
                this.txMapper.update(CoinType.getDatabase(getCoinType()),tx);
            }

        }
    }

    private void processSendedAndTimeoutTx(){
        List<EthereumTransaction> txList = this.txMapper.
                queryTxByStatus(CoinType.getDatabase(getCoinType()),TxStatus.SENT.ordinal());

        for(EthereumTransaction tx:txList){
            //如果有发送完后未被确认的交易超过10分钟 则超时了 要查看链上是否已经执行过并且执行失败
            if((System.currentTimeMillis()-tx.getCreateTs().getTime())>Constants.ETHTXTIMEOUT ){

                try {
                    TransactionReceipt receipt =
                            Web3.getInstance(getCoinType()).getTransactionReceiptByTxhash(tx.getTxId());
                    //如果交易执行状态返回为null 跳过不处理
                    if(StringUtils.isEmpty(receipt.getStatus()))continue;
                    //执行失败 减去花掉的手续费
                    if("0x0".equals(receipt.getStatus())){
                        WalletAccountBalance account =
                                this.balanceMapper.findBalanceByContractAddressAndCoinType(CoinType.getDatabase(getCoinType()),
                                        getCoinType(),tx.getContractAddress(),tx.getFrom());

                        BigInteger gasCost = receipt.getGasUsed().multiply(tx.getGasPrice());

                        account.setTotalBalance(account.getTotalBalance().subtract(gasCost).add(tx.getValue()));

                        account.setFrozenBalance(account.getFrozenBalance().subtract(tx.getValue()).subtract(
                                tx.getGasPrice().multiply(tx.getGasLimit())
                        ));
                        tx.setStatus(TxStatus.CHAINFAILED.ordinal());
                        txMapper.update(CoinType.getDatabase(getCoinType()),tx);
                    }
                } catch (Exception e) {
                    log.error("process ethfamily timeout tx:"+tx.getTxId(),e);
                }
            }

        }
    }

    @Override
    public void updateAccountBalanceByConfirmTx() {
        Long lastedBlock = this.blockMapper.queryMaxBlockInDb(CoinType.getDatabase(getCoinType()));

        //先处理 确认中和孤立再确认中这两个状态的tx
        processConrimingTx(lastedBlock);

        processSendedAndTimeoutTx();

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

            String result[] = new String[2];
            result[0] = "0x"+toAddress;
            result[1] = new BigInteger(value,16).toString(10);
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


        List<EthereumTransaction> list=new ArrayList<>();
        List<WalletAccountBind> notContractAccounts = getEthAccounts(results);
        if(!CollectionUtils.isEmpty(list)){
            needAddList.addAll(list);
        }

            for(EthBlock.TransactionResult r:results)
            {
                EthBlock.TransactionObject tx = (EthBlock.TransactionObject) r.get();
                EthereumTransaction dbTx = new EthereumTransaction();
                if(tx.getTo()==null){
                    log.info("is not erc20 contract:"+tx.getHash());
                }
                if(isContract(erc20,tx.getTo())){//如果是ERC20

                    //链上处理失败 扫块逻辑不用处理 账户处理job会处理此种情况
                    //https://etherscan.io/tx/0xc00e08d2df4dcceee72ab54b1bb5f7ad2c1d5e051a6004157d1da9355ba1e860
                    if("0x".equals(tx.getInput())){
                        continue;
                    }else {
                        String result[] = this.processInput(tx.getInput().toLowerCase());

                        if(result!=null){
                            //如果从inputData解析出的账户属于ERC20或from属于ERC20
                            dbTx.transEthTransaction(tx);
                            dbTx.setTo(result[0]);
                            dbTx.setValue( new BigInteger(result[1],10));
                            Set<String> address = new HashSet<>();
                            address.add(dbTx.getFrom());
                            address.add(dbTx.getTo());
                            List<WalletAccountBind> accoutns = accountBindMapper.queryWalletByAddress(address);
                            if(accoutns.size()>0){
                                needAddList.add(dbTx);
                            }
                        }else {
                            log.error("contract not support:"+tx.getHash());
                        }
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
