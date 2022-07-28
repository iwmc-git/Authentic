package pw.iwmc.authentic.api.configuration.nodes;

import pw.iwmc.authentic.api.configuration.enums.StorageType;

import java.util.Map;

public interface AuthenticStorageConfig {
    StorageType storageType();

    String hostname();
    String username();
    String password();
    String database();

    int port();

    Map<String, Object> props();
}
