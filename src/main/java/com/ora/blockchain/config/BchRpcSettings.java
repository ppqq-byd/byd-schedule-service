package com.ora.blockchain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rpc.bch")
public class BchRpcSettings {
    private String url;
    private String username;
    private String password;
}
