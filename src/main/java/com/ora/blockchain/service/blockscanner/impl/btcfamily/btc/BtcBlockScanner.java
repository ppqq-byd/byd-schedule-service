package com.ora.blockchain.service.blockscanner.impl.btcfamily.btc;

import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBind;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.blockscanner.impl.btcfamily.BtcfamilyBlockScanner;
import com.ora.blockchain.service.rpc.IRpcService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("btcBlockScanner")
public class BtcBlockScanner extends BtcfamilyBlockScanner {
    @Resource
    @Qualifier("btcRpcServiceImpl")
    private IRpcService rpcService;
    @Resource
    @Qualifier("btcBlockServiceImpl")
    private IBlockService blockService;

    @Override
    public IBlockService getBlockService() {
        return blockService;
    }

    @Override
    public IRpcService getRpcService() {
        return rpcService;
    }

    @Override
    public String getCoinType() {
        return Constants.COIN_TYPE_BTC;
    }

    @Override
    public List<WalletAccountBind> getWalletAccountBindByCoinType(String coinType) {
        return null;
    }

    @Override
    public void updateAccountBalance(List<WalletAccountBind> list) {

    }
}
