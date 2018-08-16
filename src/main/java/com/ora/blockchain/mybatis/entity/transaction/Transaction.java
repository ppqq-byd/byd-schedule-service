package com.ora.blockchain.mybatis.entity.transaction;

import com.ora.blockchain.constants.Constants;
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
    private Integer status = Constants.TXSTATUS_CONFIRMING;
    private Date createTs;
    private Date updateTs;
    private List<Output> outputList;
    private List<Input> inputList;

    @Override
    public int hashCode() {
        return txid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Transaction){
            Transaction t = (Transaction)obj;
            return txid.equals(t.getTxid());
        }
        return super.equals(obj);
    }
}

//    {
//        "result": {
//        "hex": "0100000001ce9f825e939beb090f936fc130c315be217292c4976cb4fe9625253756ac88e2000000006b4830450221009772c747ba3c2a5c2162eaff8add484edf3c1bdce8479a571b90f00d720b9de002206af766771a86afa9fb1c97eea1557b7a27881a11331940ce84ef599ceef131c3012103d55cf6ffdc8f366ee52b5e4a41e0a4547acb6339dae135a0fd9a3329b9b653c5ffffffff01d2fd1200000000001976a9149ffa05271ba623856562b472b5ac0f626a9ac11488ac00000000",
//        "txid": "da4f7b2f48ff37ce0aef896de8cd91903c389dca55ad6c6e394545fd9bd03fe5",
//        "size": 192,
//        "version": 1,
//        "locktime": 0,
//        "vin": [
//        {
//        "txid": "e288ac5637252596feb46c97c4927221be15c330c16f930f09eb9b935e829fce",
//        "vout": 0,
//        "scriptSig": {
//        "asm": "30450221009772c747ba3c2a5c2162eaff8add484edf3c1bdce8479a571b90f00d720b9de002206af766771a86afa9fb1c97eea1557b7a27881a11331940ce84ef599ceef131c3[ALL] 03d55cf6ffdc8f366ee52b5e4a41e0a4547acb6339dae135a0fd9a3329b9b653c5",
//        "hex": "4830450221009772c747ba3c2a5c2162eaff8add484edf3c1bdce8479a571b90f00d720b9de002206af766771a86afa9fb1c97eea1557b7a27881a11331940ce84ef599ceef131c3012103d55cf6ffdc8f366ee52b5e4a41e0a4547acb6339dae135a0fd9a3329b9b653c5"
//        },
//        "sequence": 4294967295
//        }
//        ],
//        "vout": [
//        {
//        "value": 0.01244626,
//        "valueSat": 1244626,
//        "n": 0,
//        "scriptPubKey": {
//        "asm": "OP_DUP OP_HASH160 9ffa05271ba623856562b472b5ac0f626a9ac114 OP_EQUALVERIFY OP_CHECKSIG",
//        "hex": "76a9149ffa05271ba623856562b472b5ac0f626a9ac11488ac",
//        "reqSigs": 1,
//        "type": "pubkeyhash",
//        "addresses": [
//        "XqGinTVQdCGxLk4ngCmdF8D9xaXP42u5qQ"
//        ]
//        }
//        }
//        ],
//        "blockhash": "0000000000000002d2ea6262c5ae9028450a637890a11640768fcd1b6351d5e1",
//        "height": 913591,
//        "confirmations": 1,
//        "time": 1533195797,
//        "blocktime": 1533195797
//        },
//        "error": null,
//        "id": null
//    }