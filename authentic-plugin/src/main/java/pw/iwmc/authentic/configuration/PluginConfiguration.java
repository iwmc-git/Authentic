package pw.iwmc.authentic.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import pw.iwmc.authentic.api.configuration.AuthenticConfiguration;
import pw.iwmc.authentic.api.configuration.nodes.*;
import pw.iwmc.authentic.api.engine.license.LicenseServerMode;
import pw.iwmc.authentic.api.engine.login.LoginMode;

import pw.iwmc.authentic.configuration.nodes.*;

@ConfigSerializable
public class PluginConfiguration implements AuthenticConfiguration {

    @Setting("login-mode")
    protected LoginMode loginMode = LoginMode.UNIQUE;

    @Setting("license-server-mode")
    protected LicenseServerMode licenseServerMode = LicenseServerMode.MINETOOLS;

    @Setting("session-time")
    protected long sessionTime = 10080;

    @Setting("default-language")
    protected String defaultLanguage = "en_us";

    @NodeKey
    @Setting("auth-servers")
    protected PluginServersNode authServersNode = new PluginServersNode();

    @NodeKey
    @Setting("security")
    protected PluginSecurityNode securityNode = new PluginSecurityNode();

    @NodeKey
    @Setting("storage")
    protected PluginStorageNode storageNode = new PluginStorageNode();

    @NodeKey
    @Setting("caching")
    protected PluginCachingNode cachingNode = new PluginCachingNode();

    @Override
    public LoginMode loginMode() {
        return loginMode;
    }

    @Override
    public LicenseServerMode licenseServerMode() {
        return licenseServerMode;
    }

    @Override
    public String defaultLanguage() {
        return defaultLanguage.toLowerCase();
    }

    @Override
    public long sessionTime() {
        return sessionTime;
    }

    @Override
    public ServersNode authServers() {
        return authServersNode;
    }

    @Override
    public StorageNode storage() {
        return storageNode;
    }

    @Override
    public SecurityNode security() {
        return securityNode;
    }

    @Override
    public CachingNode caching() {
        return cachingNode;
    }
}
