package com.ora.blockchain.mybatis.entity.wallet;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WalletAccountBind {

    private Long id;
    private Long accountId;
    private String coinType;
    private String address;
    private Date createTs;
    private Date updateTs;

}
