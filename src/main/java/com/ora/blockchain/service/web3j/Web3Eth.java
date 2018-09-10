package com.ora.blockchain.service.web3j;

import com.ora.blockchain.config.EthereumWeb3jSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Service("ethWeb3j")
public class Web3Eth extends Web3{

    @Autowired
    private EthereumWeb3jSettings settings;

    private Web3j web3j = null;

    @Override
    public Web3j getWeb3jClient() {
        if(web3j == null){
            web3j =  Web3j.build(new HttpService(settings.getEth()));
        }

        return  web3j;
    }
}
