package com.ora.blockchain.service.blockscanner;

public interface IBlockScanner {

    public void scanBlock(Long initBlock);

    public void updateAccount();
}
