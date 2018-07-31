package com.ora.blockchain.exception;

import lombok.Data;

@Data
public class BlockchainException extends RuntimeException {
    private String code;
    private String message;

    public BlockchainException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
