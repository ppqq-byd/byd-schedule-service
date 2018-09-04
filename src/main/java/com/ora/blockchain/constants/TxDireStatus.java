package com.ora.blockchain.constants;


public enum TxDireStatus {
    INTERNAL("INTERNAL", 0),
    INPUT("INPUT", 1),
    OUTPUT("OUTPUT", 2);

    private int value;
    private String txStatus;

    TxDireStatus(String txStatus, int value) {
        this.txStatus = txStatus;
        this.value = value;
    }

}
