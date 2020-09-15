package com.example.graph.domain;

import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDomain {
    MetaClientImpl metaClient;
    StorageClient storageClient;
}
