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
//    {
//        "result": {
//        "hash": "0000000000000002d2ea6262c5ae9028450a637890a11640768fcd1b6351d5e1",
//        "confirmations": 1,
//        "size": 4901,
//        "height": 913591,
//        "version": 536870912,
//        "merkleroot": "22dfaecc6ec58331377fc614523957689807412b66c9bba308f497a4d03ea9d9",
//        "tx": [
//        "74f2397c85142622d13e0d068cc2d48950f3e8ba023d79b5fa4283a7465cb979",
//        "e25cae97fe85eb68d0204c3f69da45aed59df60bc75a13b07d71b5fbf3912811",
//        "c8fff7022bb2af1231ee13693a84a73556feec0b69ab552bb1b08c0546a1908d",
//        "e288ac5637252596feb46c97c4927221be15c330c16f930f09eb9b935e829fce",
//        "da4f7b2f48ff37ce0aef896de8cd91903c389dca55ad6c6e394545fd9bd03fe5"
//        ],
//        "time": 1533195797,
//        "mediantime": 1533195009,
//        "nonce": 3655669663,
//        "bits": "195580f0",
//        "difficulty": 50230617.66063337,
//        "chainwork": "000000000000000000000000000000000000000000000960a557d177a6d28235",
//        "previousblockhash": "0000000000000052bfd1210081a103ff2117021a31e1bea0984ae66a6c19fa13"
//        },
//        "error": null,
//        "id": null
//        }
