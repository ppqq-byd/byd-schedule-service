package com.ora.blockchain.mybatis.entity.block;

import com.ora.blockchain.mybatis.entity.transaction.EosTransaction;
import lombok.*;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EosBlock {
    private String timestamp;
    private String producer;
    private Boolean confirmed;
    private String previous;
    private String transaction_mroot;
    private String action_mroot;
    private String schedule_version;
    private String new_producers;
    private String header_extensions;
    private String producer_signature;
    private List<EosTransaction> txList;
    private String block_extensions;
    private String block_id;
    private Long block_num;
    private Long ref_block_prefix;
    private Integer status;
    private Date create_ts;
    private Date update_ts;
}
