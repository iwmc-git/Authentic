package pw.iwmc.authentic.configuration.nodes;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import pw.iwmc.authentic.api.configuration.nodes.StorageNode;
import pw.iwmc.authentic.api.engine.storage.StorageType;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class PluginStorageNode implements StorageNode {

    @Setting("type")
    protected StorageType storageType = StorageType.H2;

    @Setting("host")
    protected String host = "localhost:3306";

    @Setting("user")
    protected String user = "username";

    @Setting("password")
    protected String password = "password";

    @Setting("database")
    protected String database = "database";

    @Override
    public StorageType storageType() {
        return storageType;
    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public String user() {
        return user;
    }

    @Override
    public String password() {
        return password;
    }

    @Override
    public String database() {
        return database;
    }
}
