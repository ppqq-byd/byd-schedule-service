package com.ora.blockchain.mybatis.entity.wallet;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ERC20Sum {
    private Long sumValue;

    private Long gasUsed;
}
