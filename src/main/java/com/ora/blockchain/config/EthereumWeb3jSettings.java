package com.ora.blockchain.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rpc.web3j")
public class EthereumWeb3jSettings {
    private String eth;
    private String etc;
}
