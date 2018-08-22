package com.ora.blockchain.mybatis.entity.eth;

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

    private Long value;

    private Long gasPrice;

    private Long gasUsed;

    private String input;

    private int nonce;

    private int status;

    private int isDelete;

    private Date createTs;

    private Date updateTs;

    private String contractAddress;

    public void transEthTransaction(EthBlock.TransactionObject txObject){
        this.setTo(txObject.getTo());
        this.setFrom(txObject.getFrom());
        this.setTxId(txObject.getHash());
        this.setBlockHeight(txObject.getBlockNumber().longValue());
        this.setGasPrice(txObject.getGasPrice().longValue());
        this.setGasUsed(txObject.getGas().longValue());
        this.setValue(txObject.getValue().longValue());
        this.setNonce(txObject.getNonce().intValue());
        this.setBlockHash(txObject.getBlockHash());
        this.setInput(txObject.getInput().length()>65535?txObject.getInput().substring(0,65535):txObject.getInput());
        this.setStatus(Constants.TXSTATUS_CONFIRMING);
        this.setUpdateTs(new Date());
    }


}
