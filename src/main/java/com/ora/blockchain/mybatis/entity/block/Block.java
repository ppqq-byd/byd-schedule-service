package com.ora.blockchain.mybatis.entity.block;

import lombok.*;
import java.util.Date;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Block{
    private String blockHash;
    private String previousBlockHash;
    private String nextBlockHash;
    private String merkleroot;
    private String chainwork;
    private Long size;
    private Long height;
    private Long version;
    private Long time;
    private Long medianTime;
    private String bits;
    private Long nonce;
    private String difficulty;
    private Integer status;
    private Date createTs;
    private Date updateTs;
}
