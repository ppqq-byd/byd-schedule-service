package com.ora.blockchain.constants;

public enum OutputStatus {
    INVALID("INVALID", 0),//不能使用
    VALID("VALID",1),//未使用
    USING("USING",2),//使用中
    SPENT("SPENT",3);//已使用

    private int value;
    private String txStatus;

    OutputStatus(String txStatus, int value) {
        this.txStatus = txStatus;
        this.value = value;
    }

}
