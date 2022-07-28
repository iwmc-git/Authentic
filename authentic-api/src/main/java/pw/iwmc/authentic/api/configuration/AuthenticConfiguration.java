package pw.iwmc.authentic.api.configuration;

import pw.iwmc.authentic.api.configuration.nodes.*;

public interface AuthenticConfiguration {
    AuthenticMainConfig mainConfiguration();
    AuthenticLimboConfig limboConfiguration();
    AuthenticTotpConfig totpConfiguration();
    AuthenticSecurityConfig securityConfiguration();
    AuthenticStorageConfig storageConfiguration();
    AuthenticMessagesConfig messagesConfiguration();
    AuthenticCommandsConfig commandsConfiguration();
}
