package com.ora.blockchain.mybatis.entity.common;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ScanCursor {

    private Long id;

    private Long currentBlock;

    private Integer syncStatus;
}
