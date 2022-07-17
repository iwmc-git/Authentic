package pw.iwmc.authentic.api.configuration.nodes;

import pw.iwmc.authentic.api.engine.storage.StorageType;

import java.util.List;

public interface StorageNode {
    StorageType storageType();

    String host();
    String user();
    String password();
    String database();
}
