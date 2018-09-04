package com.ora.blockchain.service.blockscanner.impl.ethfamily;

import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.mybatis.mapper.block.EthereumBlockMapper;
import com.ora.blockchain.mybatis.mapper.transaction.EthereumERC20Mapper;
import com.ora.blockchain.mybatis.mapper.transaction.EthereumTransactionMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBalanceMapper;
import com.ora.blockchain.mybatis.mapper.wallet.WalletAccountBindMapper;
import com.ora.blockchain.service.blockscanner.impl.BlockScanner;
import com.ora.blockchain.service.web3j.Web3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("etcBlockScaner")
@Slf4j
public class EtcBlockScanner extends EthereumFamilyBlockScanner {
    @Override
    protected String getCoinType() {
        return CoinType.ETC.name();
    }
    //./geth  --fast --cache 2048 --datadir /data/blockchain/ethereumclassic/ --rpc --rpcaddr 10.10.3.226  --rpcport 8545
    // ./geth --data-dir=/data/blockchain/ethereumclassic/ --chain mainnet attach


}
