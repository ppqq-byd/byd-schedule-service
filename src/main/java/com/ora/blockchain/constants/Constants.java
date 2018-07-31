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

    public static final String COIN_TYPE_BTC = "btc";
    public static final String COIN_TYPE_LTC = "ltc";
    public static final String COIN_TYPE_ETH = "eth";
    public static final String COIN_TYPE_ETC = "etc";
    public static final String COIN_TYPE_EOS = "eos";
    public static final String COIN_TYPE_BCD = "bcd";
    public static final String COIN_TYPE_BTG = "btg";
    public static final String COIN_TYPE_DOGE = "doge";
    public static final String COIN_TYPE_BCH = "bch";
    public static final String COIN_TYPE_SBTC = "sbtc";
    public static final String COIN_TYPE_NAS = "nas";
    public static final String COIN_TYPE_XRP = "xrp";
    public static final String COIN_TYPE_DARK = "dark";
}
