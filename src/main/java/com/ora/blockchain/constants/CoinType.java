package com.ora.blockchain.constants;


import static com.ora.blockchain.constants.CoinTypeFamily.*;

public enum CoinType {
    BTC(BTCF, "coin_btc"),
    LTC(BTCF, "coin_ltc"),
    ETH(ETHF, "coin_eth"),
    ETC(ETHF, "coin_etc"),
    EOS(EOSF, "coin_eos"),
    BCD(BTCF, "coin_bcd"),
    BTG(BTCF, "coin_btg"),
    DOGE(BTCF, "coin_doge"),
    BCH(BTCF, "coin_bch"),
    IOTA(null, "coin_iota"),
    NAS(null, "coin_nas"),
    XRP(null, "coin_xrp"),
    DARK(BTCF, "coin_dark");


    private String database;
    private CoinTypeFamily family;

    CoinType(CoinTypeFamily family, String database) {
        this.family = family;
        this.database = database;
    }

    public static String getDatabase(String coinType) {
        return CoinType.valueOf(coinType).database;
    }

    public CoinTypeFamily getFamily() {
        return family;
    }
}
