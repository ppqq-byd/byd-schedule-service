package com.ora.blockchain.service.block;

import com.ora.blockchain.mybatis.entity.block.Block;

import java.util.List;

public interface IBlockService {
    public void insertBlock(String database, Block block);

    public void updateBlock(String database, List<Block> dbList, List<Block> paramList);

    public void updateBlock(String database, List<Block> paramList);

    public void updateBlock(String database, Block dbBlock,Block paramBlock);

    public List<Block> queryBlockList(String database, Long height,int size);
}
