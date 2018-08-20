package com.ora.blockchain.service.blockscanner.impl.btcfamily.dark;

import com.ora.blockchain.constants.Constants;
import com.ora.blockchain.mybatis.entity.wallet.WalletAccountBind;
import com.ora.blockchain.service.block.IBlockService;
import com.ora.blockchain.service.blockscanner.impl.btcfamily.BtcfamilyBlockScanner;
import com.ora.blockchain.service.rpc.IRpcService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("darkBlockScanner")
public class DarkBlockScanner extends BtcfamilyBlockScanner {

    @Resource
    @Qualifier("darkRpcServiceImpl")
    private IRpcService rpcService;
    @Resource
    @Qualifier("darkBlockServiceImpl")
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
        return Constants.COIN_TYPE_DARK;
    }

    @Override
    public List<WalletAccountBind> getWalletAccountBindByCoinType(String coinType) {
        return null;
    }

    @Override
    public void updateAccountBalance(List<WalletAccountBind> list) {

    }
}
