package com.ora.blockchain;


import com.ora.blockchain.config.BcdRpcSettings;
import com.ora.blockchain.config.BtcRpcSettings;
import com.ora.blockchain.config.LtcRpcSettings;
import com.ora.blockchain.config.DarkRpcSettings;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig {
    @Bean(name="darkRpcRestTemplate")
    public RestTemplate darkRpcRestTemplate(DarkRpcSettings darkRpcSettings) {
        return new RestTemplateBuilder()
                .rootUri(darkRpcSettings.getUrl())
                .basicAuthorization(darkRpcSettings.getUsername(), darkRpcSettings.getPassword())
                .build();
    }

    @Bean(name="ltcRpcRestTemplate")
    public RestTemplate ltcRpcRestTemplate(LtcRpcSettings ltcRpcSettings) {
        return new RestTemplateBuilder()
                .rootUri(ltcRpcSettings.getUrl())
                .basicAuthorization(ltcRpcSettings.getUsername(), ltcRpcSettings.getPassword())
                .build();
    }

    @Bean(name="btcRpcRestTemplate")
    public RestTemplate btcRpcRestTemplate(BtcRpcSettings btcRpcSettings) {
        return new RestTemplateBuilder()
                .rootUri(btcRpcSettings.getUrl())
                .basicAuthorization(btcRpcSettings.getUsername(), btcRpcSettings.getPassword())
                .build();
    }

    @Bean(name="bcdRpcRestTemplate")
    public RestTemplate bcdRpcRestTemplate(BcdRpcSettings bcdRpcSettings) {
        return new RestTemplateBuilder()
                .rootUri(bcdRpcSettings.getUrl())
                .basicAuthorization(bcdRpcSettings.getUsername(), bcdRpcSettings.getPassword())
                .build();
    }
}
