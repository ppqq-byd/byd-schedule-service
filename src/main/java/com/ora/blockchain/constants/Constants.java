package com.ora.blockchain.constants;

public interface Constants {

    public static final int TXSTATUS_SENDING = 0;

    public static final int TXSTATUS_SENDED = 1;

    public static final int TXSTATUS_CONFIRMING = 2;

    public static final int TXSTATUS_CONFIRMED = 3;

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

    public static final String DATABASE_COMMON = "wallet_common";
}
