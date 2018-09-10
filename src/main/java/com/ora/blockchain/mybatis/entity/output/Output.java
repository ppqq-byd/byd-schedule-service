package com.ora.blockchain.mybatis.entity.output;

import lombok.*;

import java.math.BigInteger;
import java.util.Date;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Output {
    private Long Confirmations;
    private Long height;
    private Long walletAccountId;
    private String blockHash;
    private String transactionTxid;
    private Integer coinbase;
    private Double value;
    private BigInteger valueSat;
    private Integer n;
    private String scriptPubKeyAsm;
    private String scriptPubKeyHex;
    private Integer scriptPubKeyReqSigs;
    private String scriptPubKeyType;
    private String scriptPubKeyAddresses;
    private Integer status;
    private Date createTs;
    private Date updateTs;
}