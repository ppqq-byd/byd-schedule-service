package com.ora.blockchain.mybatis.entity.transaction;

import lombok.*;
import org.web3j.protocol.core.methods.response.EthBlock;

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

    private Long blockHeight;

    private Double value;

    private Double gasPrice;

    private Double gasUsed;

    private int nonce;

    public void transEthTransaction(EthBlock.TransactionObject txObject){
        this.setTo(txObject.getTo());
        this.setFrom(txObject.getFrom());
        this.setTxId(txObject.getHash());
        this.setBlockHeight(txObject.getBlockNumber().longValue());
        this.setGasPrice(txObject.getGasPrice().doubleValue());
        this.setGasUsed(txObject.getGas().doubleValue());
        this.setValue(txObject.getValue().doubleValue());
        this.setNonce(txObject.getNonce().intValue());

    }


}
