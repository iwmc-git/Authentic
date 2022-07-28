package pw.iwmc.authentic.configuration.nodes;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import pw.iwmc.authentic.api.configuration.enums.StorageType;
import pw.iwmc.authentic.api.configuration.nodes.AuthenticStorageConfig;

import java.util.*;

@ConfigSerializable
public class StorageConfiguration implements AuthenticStorageConfig {

    @Setting("storage-type")
    protected StorageType storageType = StorageType.H2;

    @Setting("hostname")
    protected String hostname = "localhost";

    @Setting("port")
    protected int port = 3306;

    @Setting("username")
    protected String username = "username";

    @Setting("password")
    protected String password = "password";

    @Setting("database")
    protected String database = "database";

    @Setting("properties")
    protected List<String> properties = new ArrayList<>();

    @Override
    public StorageType storageType() {
        return storageType;
    }

    @Override
    public String hostname() {
        return hostname;
    }

    @Override
    public String username() {
        return username;
    }

    @Override
    public String password() {
        return password;
    }

    @Override
    public String database() {
        return database;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public Map<String, Object> props() {
        var map = new HashMap<String, Object>();

        if (properties.isEmpty()) {
            return Collections.emptyMap();
        }

        properties.forEach(s -> {
            var key = s.split(":")[0];
            var value = (Object) s.split(":")[1];

            map.put(key, value);
        });

        return map;
    }
}
