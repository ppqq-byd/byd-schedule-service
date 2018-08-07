package com.ora.blockchain.mybatis.entity.transaction;

import lombok.*;

import java.sql.Timestamp;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EthereumTransaction {

    private Long id;

    private String txId;

    private String from;

    private String to;

    private Timestamp blockTime;

    private Long blockHeight;

    private Double value;

    private Double gasPrice;

    private Double gasLimit;

    private Double gasUsed;

    private int nonce;



}
