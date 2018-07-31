package com.ora.blockchain.mybatis.entity.transaction;

import com.ora.blockchain.mybatis.entity.input.Input;
import com.ora.blockchain.mybatis.entity.output.Output;
import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String txid;
    private String hex;
    private Long size;
    private Long version;
    private Long locktime;
    private Long height;
    private Long time;
    private String blockHash;
    private Long blockTime;
    private Integer status;
    private Date createTs;
    private Date updateTs;
    private List<Output> outputList;
    private List<Input> inputList;
}