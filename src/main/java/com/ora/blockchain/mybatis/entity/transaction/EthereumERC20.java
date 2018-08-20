package com.ora.blockchain.mybatis.entity.transaction;

import lombok.*;


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

}
