package com.ora.blockchain.mybatis.entity.output;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Output {
    public static final Integer STATUS_UNSPENT = 1;
    public static final Integer STATUS_SPENT = 0;
    private String transactionTxid;
    private Double value;
    private Long valueSat;
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