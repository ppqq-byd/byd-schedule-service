package com.ora.blockchain.mybatis.entity.eth;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EthereumScanCursor {

    private Long id;

    private Long currentBlock;

    private Integer syncStatus;
}
