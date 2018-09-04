package com.ora.blockchain.constants;


public enum TxStatus {
    SENDING("SENDING", 0),
    SENT("SENT", 1),
    CONFIRMING("CONFIRMING", 2),
    COMPLETE("COMPLETE", 3),
    ISOLATED("ISOLATED", 4),
    ISOLATEDCONRIMING("ISOLATEDCONRIMING", 5),
    CHAINFAILED("CHAINFAILED", 6);

    private int value;
    private String txStatus;

    TxStatus(String txStatus, int value) {
        this.txStatus = txStatus;
        this.value = value;
    }

}
