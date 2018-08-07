package com.ora.blockchain.mybatis.entity.block;


import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EthereumBlock {

    private String hash;

    private String parentHash;

    private Long difficulty;

    private Long blockNumber;

    private int confirmNumber;

    private Date blockTime;

}
