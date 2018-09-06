package com.ora.blockchain.service.blockscanner.impl.ethfamily;


import com.ora.blockchain.constants.CoinType;
import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.mybatis.entity.block.EthereumBlock;
import com.ora.blockchain.mybatis.entity.eth.EthereumERC20;
import com.ora.blockchain.mybatis.entity.eth.EthereumTransaction;
import com.ora.blockchain.mybatis.entity.wallet.ERC20Sum;
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
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@Service("ethBlockScaner")
@Slf4j
public class EthereumBlockScanner extends EthereumFamilyBlockScanner {


    @Resource
    @Qualifier("ethWeb3j")
    private Web3 web3eth;

    @Override
    protected String getCoinType() {
        return CoinType.ETH.name();
    }

    @Override
    public Web3 getWeb3Client() {
        return web3eth;
    }
}
