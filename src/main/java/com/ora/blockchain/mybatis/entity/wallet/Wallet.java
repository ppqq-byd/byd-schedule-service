package com.ora.blockchain.mybatis.entity.wallet;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    private long walletAccountId;
    private String walletAccount;
    private String address;
}
