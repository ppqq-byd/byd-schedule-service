package com.ora.blockchain.service.rpc.impl;

import lombok.Data;

@Data
public class RpcResponse<T> {
    private T result;
    private Object error;
    private String id;
}
