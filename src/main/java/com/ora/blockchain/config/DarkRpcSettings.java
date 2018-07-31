package com.ora.blockchain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rpc.dark")
public class DarkRpcSettings {
    private String url;
    private String username;
    private String password;
}
