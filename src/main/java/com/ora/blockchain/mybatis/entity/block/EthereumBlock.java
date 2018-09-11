package com.ora.blockchain.mybatis.entity.block;


import lombok.*;
import org.web3j.protocol.core.methods.response.EthBlock;

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

    private Date blockTime;

    public void trans(EthBlock ethBlock){

        this.setBlockNumber(ethBlock.getBlock().getNumber().longValue());
        if(ethBlock.getBlock().getTimestamp().longValue()==0){
            this.setBlockTime(new Date());
        }else {
            //以太坊的时间戳单位是秒 所以乘以1000
            this.setBlockTime(new Date(ethBlock.getBlock().getTimestamp().longValue()*1000L));

        }

        this.setDifficulty(ethBlock.getBlock().getDifficulty().longValue());
        this.setHash(ethBlock.getBlock().getHash());
        this.setParentHash(ethBlock.getBlock().getParentHash());

    }
}
