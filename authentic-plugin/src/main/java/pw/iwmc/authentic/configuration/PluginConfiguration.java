package pw.iwmc.authentic.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import pw.iwmc.authentic.api.configuration.AuthenticConfiguration;
import pw.iwmc.authentic.configuration.nodes.*;

@ConfigSerializable
public class PluginConfiguration implements AuthenticConfiguration {

    @NodeKey
    @Setting("main-settings")
    protected MainConfiguration mainConfiguration = new MainConfiguration();

    @NodeKey
    @Setting("limbo-settings")
    protected LimboConfiguration limboConfiguration = new LimboConfiguration();

    @NodeKey
    @Setting("totp-settings")
    protected TotpConfiguration totpConfiguration = new TotpConfiguration();

    @NodeKey
    @Setting("security-settings")
    protected SecurityConfiguration securityConfiguration = new SecurityConfiguration();

    @NodeKey
    @Setting("storage-settings")
    protected StorageConfiguration storageConfiguration = new StorageConfiguration();

    @NodeKey
    @Setting("messages-settings")
    protected MessagesConfiguration messagesConfiguration = new MessagesConfiguration();

    @NodeKey
    @Setting("commands-settings")
    protected CommandsConfiguration commandsConfiguration = new CommandsConfiguration();

    @Override
    public MainConfiguration mainConfiguration() {
        return mainConfiguration;
    }

    @Override
    public LimboConfiguration limboConfiguration() {
        return limboConfiguration;
    }

    @Override
    public TotpConfiguration totpConfiguration() {
        return totpConfiguration;
    }

    @Override
    public SecurityConfiguration securityConfiguration() {
        return securityConfiguration;
    }

    @Override
    public StorageConfiguration storageConfiguration() {
        return storageConfiguration;
    }

    @Override
    public MessagesConfiguration messagesConfiguration() {
        return messagesConfiguration;
    }

    @Override
    public CommandsConfiguration commandsConfiguration() {
        return commandsConfiguration;
    }
}
