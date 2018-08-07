package com.ora.blockchain.constants;

public interface Constants {
    String INTERNAL_ERROR = "500";
    String SUCCESS = "10000";
    String SUCCESS_MSG = "SUCCESS";

    String ORA_SQL_EXCEPTION = "601";
    String ORA_SQL_EXCEPTION_MSG = "数据库异常!";

    String ORA_CONNECTION_EXCEPTION = "602";
    String ORA_CONNECTION_EXCEPTION_MSG = "网络异常!";

    String ORA_REQUIRED_PARAM_EXCEPTION = "603";
    String ORA_REQUIRED_PARAM_EXCEPTION_MSG = "请检查是否有参数未输入!";

    String ORA_RPC_EXCEPTION = "701";
    String ORA_RPCEXCEPTION_MSG = "RPC接口调用异常";

    public static final String COIN_TYPE_BTC = "coin_btc";
    public static final String COIN_TYPE_LTC = "coin_ltc";
    public static final String COIN_TYPE_ETH = "coin_eth";
    public static final String COIN_TYPE_ETC = "coin_etc";
    public static final String COIN_TYPE_EOS = "coin_eos";
    public static final String COIN_TYPE_BCD = "coin_bcd";
    public static final String COIN_TYPE_BTG = "coin_btg";
    public static final String COIN_TYPE_DOGE = "coin_doge";
    public static final String COIN_TYPE_BCH = "coin_bch";
    public static final String COIN_TYPE_SBTC = "coin_sbtc";
    public static final String COIN_TYPE_NAS = "coin_nas";
    public static final String COIN_TYPE_XRP = "coin_xrp";
    public static final String COIN_TYPE_DARK = "coin_dark";
    
    public static final String DATABASE_COMMON = "wallet_common";
}
