package com.ora.blockchain.mybatis.entity.wallet;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WalletAccountBalance {
    private Long id;
    private Long accountId;
    private String coinType;
    private Integer tokenId;
    private String totalBalance;
    private String frozenBalance;
    private Date createTs;
    private Date updateTs;
}
