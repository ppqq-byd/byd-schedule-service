package com.ora.blockchain.mybatis.entity.transaction;

import lombok.*;

import java.util.Date;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EosTransaction {
    private String tx_status;
    private String cpu_usage_us;
    private String net_usage_words;
    private String tx_id;
    private String account;
    private String name;
    private String authorization;
    private String from;
    private String to;
    private String quantity;
    private String memo;
    private String hex_data;
    private String signatures;
    private String compression;
    private String packed_context_free_data;
    private String context_free_data;
    private String packed_trx;
    private String expiration;
    private Long ref_block_num;
    private Long ref_block_prefix;
    private Long max_net_usage_words;
    private Long max_cpu_usage_ms;
    private Long delay_sec;
    private String context_free_actions;
    private String actions;
    private String transaction_extensions;
    private Integer status;
    private Date create_ts;
    private Date update_ts;
}
