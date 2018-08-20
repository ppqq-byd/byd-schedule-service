package com.ora.blockchain.service.blockscanner;

public interface IBlockScanner {

    public void scanBlock(Long initBlock) throws Exception;

    public void updateAccount(String coinType);
}
