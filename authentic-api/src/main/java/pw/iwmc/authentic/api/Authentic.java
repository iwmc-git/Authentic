package pw.iwmc.authentic.api;

import pw.iwmc.authentic.api.configuration.AuthenticConfiguration;

import pw.iwmc.authentic.api.managers.AuthenticAccountManager;
import pw.iwmc.authentic.api.managers.AuthenticLicenseManager;
import pw.iwmc.authentic.api.managers.AuthenticStorageManager;

public interface Authentic {
    AuthenticConfiguration configuration();

    AuthenticAccountManager accountManager();
    AuthenticStorageManager storageManager();
    AuthenticLicenseManager licenseManager();

    static Authentic authentic() {
        try {
            var method = Class.forName("pw.iwmc.authentic.VelocityAuthentic").getDeclaredMethod("authentic");
            return (Authentic) method.invoke(null);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
