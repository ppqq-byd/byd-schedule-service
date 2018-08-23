package com.ora.blockchain.service.blockscanner.impl.eosfamily;

import client.EosApiClientFactory;
import client.EosApiRestClient;
import client.domain.response.chain.Block;
import client.domain.response.history.transaction.Transaction;
import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.mybatis.entity.block.EosBlock;
import com.ora.blockchain.mybatis.entity.transaction.EosTransaction;
import com.ora.blockchain.mybatis.entity.wallet.Wallet;
import com.ora.blockchain.mybatis.mapper.block.EosBlockMapper;
import com.ora.blockchain.mybatis.mapper.transaction.EosTransactionMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletMapper;
import com.ora.blockchain.service.blockscanner.impl.BlockScanner;
import com.ora.blockchain.utils.EosUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EosBlockScanner extends BlockScanner {

    @Autowired
    private EosBlockMapper blockMapper;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private EosTransactionMapper transMapper;

    EosApiRestClient eosApiRestClient = EosApiClientFactory.newInstance("http://127.0.0.1:8888").newRestClient();

    @Override
    public void deleteBlockAndUpdateTx(Long blockHeight) {
        return;
    }

    @Override
    public Long getNeedScanBlockHeight(Long initBlockHeight) {
        EosBlock block = blockMapper.queryLastBlock(getCoinType());
        return null != block ? block.getBlock_num() + 1L : initBlockHeight;
    }

    @Override
    public boolean verifyIsolatedBlock(Long needScanBlock) {
        return false;
    }

    @Override
    public void syncBlockAndTx(Long blockHeight) {
        Block block = eosApiRestClient.getBlock(eosApiRestClient.getChainInfo().getHeadBlockId());
        if(null == block)
            return;
        EosBlock eosBlock = new EosBlock();
        eosBlock.setTimestamp(block.getTimeStamp());
        eosBlock.setProducer(block.getProducer());
        eosBlock.setConfirmed(block.getConfirmed());
        eosBlock.setPrevious(block.getPrevious());
        eosBlock.setTransaction_mroot(block.getTransactionMerkleRoot());
        eosBlock.setAction_mroot(block.getActionMerkleRoot());
        eosBlock.setSchedule_version(block.getScheduleVersion());
        eosBlock.setNew_producers(StringUtils.join(block.getNewProducers(), "+"));
        eosBlock.setHeader_extensions(StringUtils.join(block.getHeaderExtensions(), "+"));
        eosBlock.setProducer_signature(block.getProducerSignature());
        eosBlock.setBlock_extensions(StringUtils.join(block.getBlockExtensions(), "+"));
        eosBlock.setBlock_id(block.getId());
        eosBlock.setBlock_num(block.getBlockNum());
        eosBlock.setRef_block_prefix(block.getRefBlockPrefix());
        blockMapper.insertBlock(getCoinType(), eosBlock);
        List<EosTransaction> transList = new ArrayList<>();
        List<String> addressList = new ArrayList<>();
        for(Transaction t:block.getTransactions()){
            EosTransaction tx = new EosTransaction();
            Map<String, String> trx = (Map)t.getTrx();
            tx.setTx_status(t.getStatus());
            tx.setCpu_usage_us(t.getCpuUsageUs());
            tx.setNet_usage_words(t.getNetUsageWords());
            tx.setTx_id(trx.get("id"));
            List signaturesList = EosUtils.getValueList((Map)trx, "signatures");
            tx.setSignatures(String.join("+", signaturesList));
            tx.setCompression(EosUtils.getValue((Map)(t.getTrx()), "compression"));
            tx.setPacked_context_free_data(EosUtils.getValue((Map)(t.getTrx()), "packed_context_free_data"));
            List context_free_data_list = EosUtils.getValueList((Map)(t.getTrx()), "context_free_data");
            tx.setContext_free_data(String.join("+", EosUtils.listToArrayList(context_free_data_list)));
            tx.setPacked_trx(EosUtils.getValue((Map)(t.getTrx()), "packed_trx"));
            tx.setExpiration(EosUtils.getValue((Map)(t.getTrx()), "transaction/expiration"));
            tx.setRef_block_num(Long.valueOf(EosUtils.getValue((Map)(t.getTrx()), "transaction/ref_block_num")));
            tx.setRef_block_prefix(Long.valueOf(EosUtils.getValue((Map)(t.getTrx()), "transaction/ref_block_prefix")));
            tx.setMax_net_usage_words(Long.valueOf(EosUtils.getValue((Map)(t.getTrx()), "transaction/max_net_usage_words")));
            tx.setMax_cpu_usage_ms(Long.valueOf(EosUtils.getValue((Map)(t.getTrx()), "transaction/max_cpu_usage_ms")));
            tx.setDelay_sec(Long.valueOf(EosUtils.getValue((Map)(t.getTrx()), "transaction/delay_sec")));
            List context_free_actions_list = EosUtils.getValueList((Map)(t.getTrx()), "transaction/context_free_actions");
            tx.setContext_free_actions(String.join("+", EosUtils.listToArrayList(context_free_actions_list)));
            List transaction_extensions_list =  EosUtils.getValueList((Map)(t.getTrx()),"transaction/transaction_extensions");
            tx.setTransaction_extensions(String.join("+", EosUtils.listToArrayList(transaction_extensions_list)));
            List actionsList =  EosUtils.getValueList((Map)(t.getTrx()),"transaction/actions");
            tx.setActions(String.join("+", EosUtils.listToArrayList(actionsList)));
            for (int i = 0; i < actionsList.size(); i++){
                String account = EosUtils.getValue((Map)actionsList.get(i), "account");
                tx.setAccount(account);
                String name = EosUtils.getValue((Map)actionsList.get(i), "name");
                tx.setName(name);
                if (account.equals("eosio.token") && name.equals("transfer")){
                    List authorizationList = EosUtils.getValueList((Map)actionsList.get(i),"authorization");
                    String authorization = String.join("+", EosUtils.listToArrayList(authorizationList));
                    tx.setAuthorization(authorization);
                    String from = EosUtils.getValue((Map)actionsList.get(i), "data/from");
                    tx.setFrom(from);
                    String to = EosUtils.getValue((Map)actionsList.get(i), "data/to");
                    tx.setTo(to);
                    String quantity = EosUtils.getValue((Map)actionsList.get(i), "data/quantity");
                    tx.setQuantity(quantity);
                    String memo = EosUtils.getValue((Map)actionsList.get(i), "data/memo");
                    tx.setMemo(memo);
                    String hex_data = EosUtils.getValue((Map)actionsList.get(i), "hex_data");
                    tx.setHex_data(hex_data);
                    addressList.add(from);
                    addressList.add(to);
                    transList.add(tx);
                }
            }
        }
        if(null == addressList || addressList.isEmpty()){
            return;
        }
        List<Wallet> walletList = walletMapper.queryWalletByAddress(addressList);
        if(null == walletList || walletList.isEmpty()){
            return;
        }
        Map<String,Long> walletMap = walletList.stream().collect(Collectors.toMap(Wallet::getAddress,Wallet::getWalletAccountId));
        List<EosTransaction> txList = filterTransaction(transList, walletMap.keySet());
        if (null != txList && !txList.isEmpty()){
            transMapper.insertTransactionList(getCoinType(), txList);
        }
    }

    @Override
    public boolean isNeedScanHeightLasted(Long needScanBlock) {
        Long height = eosApiRestClient.getBlock(eosApiRestClient.getChainInfo().getHeadBlockId()).getBlockNum();
        return null != needScanBlock && null != height && needScanBlock.longValue() >= height.longValue();
    }

    public String getCoinType() {
        return Constants.COIN_TYPE_EOS;
    }

    private List<EosTransaction> filterTransaction(List<EosTransaction> blockchainTrans, Set<String> addressSet) {
        if (null == blockchainTrans || blockchainTrans.isEmpty() || null == addressSet || addressSet.isEmpty()) {
            return null;
        }

        List<EosTransaction> transList = new ArrayList<>();
        blockchainTrans.forEach((EosTransaction t) -> {
           if (addressSet.contains(t.getFrom()) || addressSet.contains(t.getTo())){
               transList.add(t);
           }
        });

        return transList;
    }
}
