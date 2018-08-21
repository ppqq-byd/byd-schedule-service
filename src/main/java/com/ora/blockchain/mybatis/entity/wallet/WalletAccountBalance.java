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
    private Long totalBalance = 0L;
    private Long frozenBalance = 0L;
    private Date createTs;
    private Date updateTs;
}
