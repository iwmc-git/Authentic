package pw.iwmc.authentic.api.configuration;

import pw.iwmc.authentic.api.configuration.nodes.*;
import pw.iwmc.authentic.api.engine.login.LoginMode;
import pw.iwmc.authentic.api.engine.license.LicenseServerMode;

public interface AuthenticConfiguration {
    LoginMode loginMode();
    LicenseServerMode licenseServerMode();

    String defaultLanguage();

    long sessionTime();
    boolean debug();

    ServersNode authServers();
    StorageNode storage();
    SecurityNode security();
    CachingNode caching();
}
