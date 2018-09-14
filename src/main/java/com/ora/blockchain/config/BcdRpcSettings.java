package com.ora.blockchain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rpc.bcd")
public class BcdRpcSettings {
    private String url;
    private String username;
    private String password;
}
