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
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@Service
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



    protected abstract String getCoinType();

    public abstract Web3 getWeb3Client();

    @Override
    public boolean isNeedScanHeightLasted(Long needScanBlock) throws IOException {
        try {
            if(needScanBlock>getWeb3Client().getCurrentBlockHeight().longValue())
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
        if(dbBlockHeight==-1){
            dbBlockHeight = initBlockHeight;
        }else {
            dbBlockHeight = dbBlockHeight + 1;
        }
        return dbBlockHeight;
    }

    @Override
    public boolean verifyIsolatedBlock(Long needScanBlock) throws Exception {

        if(needScanBlock==0)return false;
        //现有数据库中最后一个块
        EthereumBlock dbBlock = blockMapper.
                queryEthBlockByBlockNumber(CoinType.getDatabase(getCoinType()),(needScanBlock));

        //与节点中的对比
        EthBlock block = getWeb3Client().getBlockInfoByNumber(needScanBlock-1);


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
                                                  List<EthereumTransaction> inDbTx,Map<String,EthereumERC20> erc20Map){

        List<EthereumTransaction> needUpdateTx = new ArrayList<>();

        List<EthereumTransaction> needInsertTx = null;

        Iterator<EthereumTransaction> it = filteredTx.iterator();
        while (it.hasNext()){
            EthereumTransaction tx = it.next();
            tx.setStatus(TxStatus.CONFIRMING.ordinal());
            tx.setIsDelete(0);

            boolean isDelete = false;
            for(EthereumTransaction dbTx:inDbTx){

                if(dbTx.getTxId().equals(tx.getTxId())){

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
        for(EthereumTransaction tx:needInsertTx){//检查发送成功 但是数据库没记录上的数据
            if(tx.getIsSender()==1&&tx.getContractAddress()!=null){
                BigInteger transValue = tx.getValue().
                        multiply(erc20Map.get(tx.getContractAddress()).getDecimalBigInteger());
                tx.setValue(transValue);
            }

        }
        map.put("insert",needInsertTx);
        map.put("update",needUpdateTx);

        return map;
    }

    /**
     * 判断是否是合约
     * @param erc20Map
     * @param address
     * @return
     */
    private boolean isContract( Map<String,EthereumERC20> erc20Map,String address){
        if(address==null)return false;
        if(erc20Map.get(address.toLowerCase())!=null){
            return true;
        }
        return false;
    }

    /**
     * 将list转成map
     * @param erc20
     * @return
     */
    private Map<String,EthereumERC20> transERC20ToMap(List<EthereumERC20> erc20){
        Map<String,EthereumERC20> map = new HashMap<>();
        for(EthereumERC20 erc:erc20){
            map.put(erc.getContractAddress().toLowerCase(),erc);
        }
        return map;
    }

    @Override
    public void syncBlockAndTx(Long blockHeight) throws Exception {
        log.info("*********************************syncBlockAndTx:"+this.getCoinType());
        EthBlock block = getWeb3Client().getBlockInfoByNumber(blockHeight);
        EthereumBlock dbBlock = new EthereumBlock();
        dbBlock.trans(block);
        blockMapper.insertBlock(CoinType.getDatabase(getCoinType()),dbBlock);

        if(block.getBlock().getTransactions()==null
        ||block.getBlock().getTransactions().size()==0){
            log.info("empty block:"+blockHeight);
            return;
        }

        //获取erc20的定义
        List<EthereumERC20> erc20 = erc20Mapper.queryERC20(CoinType.getDatabase(getCoinType()));
        Map<String,EthereumERC20> erc20Map = transERC20ToMap(erc20);
        //过滤掉非系统账户的tx 以及 非支持的ERC20的tx
        List<EthereumTransaction> filteredTx = filterTx(block,erc20Map);
        System.out.println("filteredTx:"+filteredTx.size());

        if(filteredTx==null||filteredTx.size()==0)return;
        //找出已在DB中存在的tx
        List<EthereumTransaction> inDbTx = txMapper.queryTxInDb(CoinType.getDatabase(getCoinType()),filteredTx);
        //根据inDBtx集合 将filteredTx处理为 待update和待insert两个集合
        Map<String, List<EthereumTransaction> > map = processTxStatus(filteredTx,inDbTx,erc20Map);

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
                log.warn("balance;" +getCoinType()+
                        ";inner-receive;account:"+in.getAccountId()+";amount:"+tx.getValue());
            }

            //冻结减去gasLimit*gasPrice
            out.setFrozenBalance(out.getFrozenBalance().
                    subtract(tx.getGasPrice().multiply(tx.getGasLimit()==null?BigInteger.valueOf(0L):tx.getGasLimit())));

            //账户减去交易的amount再减去gasUsed
            out.setTotalBalance(out.getTotalBalance().subtract(tx.getValue()).subtract(
                    tx.getGasPrice().multiply(tx.getGasUsed())
            ));

            balanceMapper.update(out);

            log.warn("balance;" +getCoinType()+
                    ";send;account:"+out.getAccountId()+";amount:"+tx.getValue());

            log.warn("balance;" +getCoinType()+
                    ";sendgas:"+out.getAccountId()+";amount:"+tx.getGasPrice().multiply(tx.getGasUsed()));
        }else{//如果是收币

            WalletAccountBalance in =
                    balanceMapper.findBalanceOfCoinByAddressAndCointype(tx.getTo(),getCoinType());

            in.setTotalBalance(in.getTotalBalance().add(tx.getValue()));

            balanceMapper.update(in);

            log.warn("balance;" +getCoinType()+
                    ";receive;account:"+in.getAccountId()+";amount:"+tx.getValue());
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
                    log.warn("balance;token;innerreceive;account:"+account.getAccountId()+";token:"+account.getTokenId()+";amount:"+tx.getValue());
                }

                //这里处理代币的账户减钱
                account.setTotalBalance(account.getTotalBalance().subtract(tx.getValue()));
                account.setFrozenBalance(account.getFrozenBalance().subtract(tx.getValue()));

                //这里处理代币所属账户的ETH账户更新gas费用
                WalletAccountBalance ethAccount = balanceMapper.findBalanceOfCoinByAddressAndCointype(tx.getFrom(),
                            getCoinType());

                //冻结的手续费为gasLimit*gasPrice
                ethAccount.setFrozenBalance(ethAccount.getFrozenBalance().
                        subtract((tx.getGasLimit()==null?BigInteger.valueOf(0L):tx.getGasLimit()).multiply(tx.getGasPrice())));
                //账户的手续费为gasUsed
                BigInteger gasUsed = tx.getGasUsed().multiply(tx.getGasPrice());
                ethAccount.setTotalBalance(ethAccount.getTotalBalance().subtract(gasUsed));
                balanceMapper.update(ethAccount);
                log.warn("balance;token;send;account:"+account.getAccountId()+
                        ";token:"+account.getTokenId()+";amount:"+tx.getValue());
            log.warn("balance;token-gas;send;account:"+account.getAccountId()+
                    ";token:"+account.getTokenId()+";amount:"+gasUsed);
            }else {
                account.setTotalBalance(account.getTotalBalance().add(tx.getValue()));
                log.warn("balance;token;receive;account:"+account.getAccountId()+";token:"+account.getTokenId()+";amount:"+tx.getValue());
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

    private void processConfirmingTx(Long lastedBlock){
        List<EthereumTransaction> txList = this.txMapper.queryTxByStatus(CoinType.getDatabase(getCoinType()),TxStatus.CONFIRMING.ordinal());
        List<EthereumTransaction> isolatedTxList = this.txMapper.queryTxByStatus(CoinType.getDatabase(getCoinType()),TxStatus.ISOLATEDCONRIMING.ordinal());
        txList.addAll(isolatedTxList);

        for(EthereumTransaction tx:txList){
            if(lastedBlock - tx.getBlockHeight()>=Constants.ETH_THRESHOLD){
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

        long before10min = System.currentTimeMillis()-Constants.ETHTXTIMEOUT ;
        Date timeoutTime = new Date(before10min);
        //如果有发送完后未被确认的交易超过10分钟 则超时了 要查看链上是否已经执行过并且执行失败
        List<EthereumTransaction> txList = this.txMapper.queryTimeoutTxBySentAndIsolate(CoinType.getDatabase(getCoinType()),
                TxStatus.SENT.ordinal(),TxStatus.ISOLATED.ordinal(),timeoutTime);

        for(EthereumTransaction tx:txList){

                try {
                    TransactionReceipt receipt =
                            getWeb3Client().getTransactionReceiptByTxhash(tx.getTxId());
                    //如果交易执行状态返回为null 跳过不处理
                    if(receipt==null||StringUtils.isEmpty(receipt.getStatus()))continue;
                    //执行失败 减去花掉的手续费
                    if("0x0".equals(receipt.getStatus())){//TODO 常量
                        WalletAccountBalance account =
                                this.balanceMapper.findBalanceByContractAddressAndCoinType(CoinType.getDatabase(getCoinType()),
                                        getCoinType(),tx.getContractAddress(),tx.getFrom());

                        BigInteger gasCost = receipt.getGasUsed().multiply(tx.getGasPrice());

                        account.setTotalBalance(account.getTotalBalance().subtract(gasCost));

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

    @Override
    public void updateAccountBalanceByConfirmTx() {
        Long lastedBlock = this.blockMapper.queryMaxBlockInDb(CoinType.getDatabase(getCoinType()));

        //先处理 确认中和孤立再确认中这两个状态的tx
        processConfirmingTx(lastedBlock);

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
    private int filterByAddress(List<WalletAccountBind> ethAccounts,EthBlock.TransactionObject tx){
        for(WalletAccountBind account:ethAccounts){
            //如果是平台账户的地址或者合约的地址
            if(account.getAddress().equals(tx.getFrom())){
                return 1;
            }else if(account.getAddress().equals(tx.getTo())){
                return 0;
            }

        }

        return -1;
    }

    /**
     * 将不属于平台账户的交易 过滤掉
     * @param block
     * @return
     */
    private List<EthereumTransaction> filterTx(EthBlock block, Map<String,EthereumERC20> erc20Map) throws Exception {

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

                WalletAccountBind wab =
                        accountBindMapper.queryEthWalletByAddress(tx.getFrom(),this.getCoinType());
                if(wab!=null){
                    dbTx.setIsSender(1);
                }else{
                    dbTx.setIsSender(0);
                }

                if(isContract(erc20Map,tx.getTo())){//如果是ERC20

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
                            EthereumERC20 erc20Define = erc20Map.get(tx.getTo().toLowerCase());
                            if(dbTx.getIsSender()==0){//如果是erc20收币的逻辑 根据位数 处理成大整数
                                dbTx.setValue( new BigInteger(result[1],10).
                                        multiply(erc20Define.getDecimalBigInteger()));
                            }

                            Set<String> address = new HashSet<>();
                            address.add(dbTx.getFrom());
                            address.add(dbTx.getTo());
                            List<WalletAccountBind> accounts = accountBindMapper.queryWalletByAddress(address);
                            if(accounts.size()>0){
                                needAddList.add(dbTx);
                            }
                            log.warn("contract tx:"+tx.getHash()+"/"+tx.getInput());
                        }else {
                            log.error("contract not support:"+tx.getHash());
                        }
                    }

                    dbTx.setContractAddress(tx.getTo());

                }else {

                    //如果是平台账户的地址
                    if(filterByAddress(notContractAccounts,tx)==0){
                        dbTx.transEthTransaction(tx);
                        dbTx.setIsSender(0);
                        needAddList.add(dbTx);
                    }else if(filterByAddress(notContractAccounts,tx)==1){
                        dbTx.transEthTransaction(tx);
                        dbTx.setIsSender(1);
                        needAddList.add(dbTx);
                    }


                }

            }


        return needAddList;
    }

}
