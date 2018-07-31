package com.ora.blockchain.mybatis.entity.input;

import lombok.*;

import java.util.Date;
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Input {
    private String transactionTxid;
    private String txid;
    private String coinbase;
    private Integer vout;
    private Long sequence;
    private String scriptSigHex;
    private String scriptSigAsm;
    private Integer status;
    private Date createTs;
    private Date updateTs;
}