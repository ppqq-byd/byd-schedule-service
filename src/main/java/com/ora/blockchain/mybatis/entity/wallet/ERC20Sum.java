package com.ora.blockchain.mybatis.entity.wallet;

import lombok.*;

import java.math.BigInteger;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ERC20Sum {
    private BigInteger sumValue;

    private BigInteger gasUsed;
}
