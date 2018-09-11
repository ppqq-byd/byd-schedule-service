package com.ora.blockchain.mybatis.entity.eth;

import lombok.*;

import java.math.BigInteger;


@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EthereumERC20 {

    private Integer id;
    private String name;
    private String shortName;
    private String contractAddress;
    private Integer decimal;
    private String officialsite;

    public BigInteger getDecimalBigInteger(){
        return new BigInteger("10").pow(this.decimal);
    }

}
