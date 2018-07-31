package com.ora.blockchain.service.rpc.impl;

import lombok.Data;

import java.util.List;

@Data
public class RpcRequest {
    private String jsonrpc = "1.0";
    private String id = "curltext";
    private String method;
    private List<Object> params;
}
