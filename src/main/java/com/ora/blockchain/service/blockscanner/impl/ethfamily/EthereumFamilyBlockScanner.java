package com.ora.blockchain.service.blockscanner.impl.ethfamily;


import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.mybatis.entity.block.EthereumBlock;
import com.ora.blockchain.mybatis.entity.common.ScanCursor;
import com.ora.blockchain.mybatis.entity.eth.EthereumERC20;
import com.ora.blockchain.mybatis.entity.eth.EthereumTransaction;
import com.ora.blockchain.mybatis.entity.wallet.ERC20Sum;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBalance;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBind;
import com.ora.blockchain.mybatis.mapper.block.EthereumBlockMapper;
import com.ora.blockchain.mybatis.mapper.common.ScanCursorMapper;
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
    private ScanCursorMapper scanCursorMapper;

    @Autowired
    private WalletAccountBalanceMapper balanceMapper;

    private static final int DEPTH = 12;

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

        txMapper.updateTransacion(CoinType.getDatabase(getCoinType()),blockHeight,null);
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
     * @param address
     * @param isOut
     */
    private WalletAccountBalance processEthCoinAccount(String address,boolean isOut,EthereumTransaction tx){
        WalletAccountBalance wc =
                balanceMapper.findBalanceOfCoinByAddressAndCointype(address,getCoinType());
        if(wc!=null){

            if(isOut){
                //TODO 内转内 内转外
                  wc.setFrozenBalance(wc.getFrozenBalance().subtract(tx.getValue()));//冻结减去值

                  wc.setTotalBalance( wc.getTotalBalance().
                          subtract(tx.getValue()).
                          subtract(tx.getGasUsed()));//真正的余额减去值

            }else{

                wc.setTotalBalance(wc.getTotalBalance().
                        add(tx.getValue()));//真正的余额加上值
            }

            balanceMapper.update(wc);
        }

        return wc;
    }

    /**
     * 处理ERC20代币
     * @param tx
     * @param out
     * @return
     */
    private WalletAccountBalance processToken(EthereumTransaction tx,boolean out){
        WalletAccountBalance account =
                this.balanceMapper.findBalanceByContractAddressAndCoinType(CoinType.getDatabase(getCoinType()),
                        getCoinType(),tx.getContractAddress(),out==true?tx.getFrom():tx.getTo());

        if(account!=null){
            if(out){
                //TODO 内转内 内转外
                //TODO gasprice*gaslimit 冻结
                account.setTotalBalance(account.getTotalBalance().subtract(tx.getValue()));

                account.setFrozenBalance(account.getFrozenBalance().subtract(tx.getValue()));
                //更新gas费用
                WalletAccountBalance ethAccount = balanceMapper.findBalanceOfCoinByAddressAndCointype(tx.getFrom(),
                        getCoinType());

                ethAccount.setTotalBalance(ethAccount.getTotalBalance().subtract(tx.getGasUsed()));
                balanceMapper.update(ethAccount);
            }else {
                account.setTotalBalance(account.getTotalBalance().add(tx.getValue()));
            }

            balanceMapper.update(account);
        }

        return account;
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

    @Override
    public void updateAccountBalanceByConfirmTx(Long scanAccountBlock) {
        Long lastedBlock = this.blockMapper.queryMaxBlockInDb(CoinType.getDatabase(getCoinType()));

        if(lastedBlock - scanAccountBlock>=DEPTH){//已经被12个块确认 需要处理
            //处理没被处理过的交易
            List<EthereumTransaction> txList =
                    this.txMapper.queryTxByBlockNumber(CoinType.getDatabase(getCoinType()),scanAccountBlock);

            HashMap<String,HashSet<String>> tokenAccounts = new HashMap<String,HashSet<String>>();

            for(EthereumTransaction tx:txList){
                //StringUtils.isEmpty()
                if(tx.getContractAddress()!=null){//处理token的逻辑
                    //转出token
                    WalletAccountBalance outTokenAccount =processToken(tx,true);
                    //转入token
                    WalletAccountBalance inTokenAccount = processToken(tx,false);
                    if(outTokenAccount!=null){
                        setAccountsMap(tokenAccounts,tx,tx.getFrom(),true);
                    }
                    if(inTokenAccount!=null){
                        setAccountsMap(tokenAccounts,tx,tx.getTo(),true);
                    }

                }else{//eth币的逻辑
                    //处理转出的逻辑
                    WalletAccountBalance outAccount = processEthCoinAccount(tx.getFrom(),true,tx);
                    //处理转入的逻辑
                    WalletAccountBalance inAccount = processEthCoinAccount(tx.getTo(),false,tx);
                    if(outAccount!=null){
                        setAccountsMap(tokenAccounts,tx,tx.getFrom(),false);
                    }
                    if(inAccount!=null){
                        setAccountsMap(tokenAccounts,tx,tx.getTo(),false);
                    }

                }

            }

            //TODO 这里什么时候 怎么做检查
            checkAccountBalance(tokenAccounts);
        }

    }

    /**
     * 检查账户是否同步正确
     * @param tokenAccounts
     */
    public void checkAccountBalance(HashMap<String,HashSet<String>> tokenAccounts){

        Set<Map.Entry<String,HashSet<String>>> set =tokenAccounts.entrySet();
        for(Map.Entry<String,HashSet<String>> entry:set){
            String ethAddress = entry.getKey();
            HashSet<String> tokens = entry.getValue();
            List<WalletAccountBalance> balanceList =
                    this.balanceMapper.findBlanceByEthAddressAndContractAddress(ethAddress,new ArrayList<>(tokens));
            //先处理token账户
            BigInteger tokenGasUsed = new BigInteger("0");
            //如果有需要处理的token账户
            if(balanceList!=null&&balanceList.size()>0){
                for(WalletAccountBalance wab:balanceList){
                    //根据地址+token id 检查tx中是否与 balance记录的一致
                    ERC20Sum result = balanceMapper.findERC20OutSumByAddressAndTokenId(ethAddress,wab.getTokenId());
                    tokenGasUsed = tokenGasUsed.add(result.getGasUsed());
                    BigInteger in = balanceMapper.findERC20InSumByAddressAndTokenId(ethAddress,wab.getTokenId());

                    if(!in.subtract(result.getSumValue()).equals(wab.getTotalBalance())){
                        //TODO
                        log.error("error;tokenBalance;coin:eth;address:"+ethAddress+";");
                        wab.setTotalBalance(in.subtract(result.getSumValue()));
                        balanceMapper.update(wab);
                    }


                }
            }

            //再检查以太坊的余额
            WalletAccountBalance ethAccountBalance =
                    balanceMapper.findBalanceOfCoinByAddressAndCointype(ethAddress,getCoinType());

            ERC20Sum ethOutSum = balanceMapper.findEthOutSumByAddress(ethAddress);

            BigInteger ethInSum = balanceMapper.findEthInSumByAddress(ethAddress);
            BigInteger checkValue =
                    ethInSum.subtract(
                            ethOutSum.getSumValue().add(ethOutSum.getGasUsed()).add(tokenGasUsed)
                    );

            if(!checkValue.equals(ethAccountBalance.getTotalBalance())){
                //TODO
                log.error("error;ethBalance;coin:eth;address:"+ethAddress+";");
               ethAccountBalance.setTotalBalance(checkValue);
               balanceMapper.update(ethAccountBalance);
            }
        }


    }

    @Override
    public Long getNeedScanAccountBlanceBlock(String coinType) {
        ScanCursor cursor = this.scanCursorMapper.getNotConfirmScanCursor(CoinType.getDatabase(getCoinType()));
        return null != cursor ? cursor.getCurrentBlock() : null;
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
                    dbTx.transEthTransaction(tx);
                    dbTx.setContractAddress(tx.getTo());
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
