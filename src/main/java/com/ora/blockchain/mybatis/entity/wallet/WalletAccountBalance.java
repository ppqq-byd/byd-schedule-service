package com.ora.blockchain.mybatis.entity.wallet;

import lombok.*;

import java.math.BigInteger;
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
    private BigInteger totalBalance;
    private BigInteger frozenBalance;
    private Date createTs;
    private Date updateTs;
}
