package pw.iwmc.authentic.api.configuration;

import pw.iwmc.authentic.api.configuration.nodes.AuthServersNode;
import pw.iwmc.authentic.api.configuration.nodes.SecurityNode;
import pw.iwmc.authentic.api.configuration.nodes.StorageNode;
import pw.iwmc.authentic.api.engine.login.LoginMode;
import pw.iwmc.authentic.api.engine.license.LicenseServerMode;

public interface AuthenticConfiguration {
    LoginMode loginMode();
    LicenseServerMode licenseServerMode();

    String defaultLanguage();

    long sessionTime();

    AuthServersNode authServers();
    StorageNode storage();
    SecurityNode security();
}
