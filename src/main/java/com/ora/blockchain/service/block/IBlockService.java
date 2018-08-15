package com.ora.blockchain.service.block;

import com.ora.blockchain.mybatis.entity.block.Block;

import java.util.List;

public interface IBlockService {
    public void insertBlock(String database, Block block);

    public void deleteByHeight(String database,Long blockHeight);

    public Block queryLastBlock(String database);
}
