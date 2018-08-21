package com.ora.blockchain.constants;

import java.util.ArrayList;
import java.util.List;

public enum CoinTypeFamily {
    BTCF, ETHF, EOSF;

    public List<CoinType> getCoinTypes() {
        List<CoinType> coinTypes = new ArrayList<>();
        for (CoinType coinType : CoinType.values()) {
            if (coinType.getFamily() == this) {
                coinTypes.add(coinType);
            }
        }
        return coinTypes;
    }
}
