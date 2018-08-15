package com.ora.blockchain.mybatis.entity.transaction;

import com.ora.blockchain.constants.Constants;
import lombok.*;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.sql.Timestamp;
import java.util.Date;

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

    private String blockHash;

    private Double value;

    private Double gasPrice;

    private Double gasUsed;

    private String input;

    private int nonce;

    private int status;

    private int isDelete;

    private Date createTs;

    private Date updateTs;

    private int isToken;

    public void transEthTransaction(EthBlock.TransactionObject txObject){
        this.setTo(txObject.getTo());
        this.setFrom(txObject.getFrom());
        this.setTxId(txObject.getHash());
        this.setBlockHeight(txObject.getBlockNumber().longValue());
        this.setGasPrice(txObject.getGasPrice().doubleValue());
        this.setGasUsed(txObject.getGas().doubleValue());
        this.setValue(txObject.getValue().doubleValue());
        this.setNonce(txObject.getNonce().intValue());
        this.setBlockHash(txObject.getBlockHash());
        this.setInput(txObject.getInput().length()>65535?txObject.getInput().substring(0,65535):txObject.getInput());
        this.setStatus(Constants.TXSTATUS_CONFIRMING);
        this.setUpdateTs(new Date());
    }


}
