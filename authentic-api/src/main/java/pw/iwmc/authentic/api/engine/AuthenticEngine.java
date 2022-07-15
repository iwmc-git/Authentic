package pw.iwmc.authentic.api.engine;

import org.jetbrains.annotations.NotNull;

import pw.iwmc.authentic.api.account.AuthenticAccount;
import pw.iwmc.authentic.api.engine.license.LicenseServerMode;
import pw.iwmc.authentic.api.engine.login.LoginMode;
import pw.iwmc.authentic.api.engine.storage.AuthenticStorage;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface AuthenticEngine {
    Map<UUID, AuthenticAccount> cachedAccounts();

    Optional<AuthenticAccount> fromCache(UUID uniqueId);

    AuthenticStorage storage();

    LoginMode currentLoginMode();
    LicenseServerMode licenseServerMode();

    void addCachedAccount(@NotNull AuthenticAccount account);
    void removeCachedAccount(@NotNull AuthenticAccount account);

    void handleAccountLogin(@NotNull AuthenticAccount account);
    void handleAccountRegister(@NotNull AuthenticAccount account);
    void handleAccountLogout(@NotNull AuthenticAccount account);
    void handleAccountLicenseState(@NotNull AuthenticAccount account);
    void handleAccountChangePassword(@NotNull AuthenticAccount account);
    void handleAccountUnregister(@NotNull AuthenticAccount account);

    void dropAccount(@NotNull AuthenticAccount account, boolean alsoForStorage);
    void truncateAccount(@NotNull AuthenticAccount account, boolean alsoForStorage);
    void makeAccount(@NotNull AuthenticAccount account, boolean alsoForStorage);
}
