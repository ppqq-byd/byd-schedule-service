package com.ora.blockchain.constants;


import static com.ora.blockchain.constants.CoinTypeFamily.*;

public enum TxStatus {
    SENDING("SENDING", 0),
    SENDED("SENDED", 1),
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
