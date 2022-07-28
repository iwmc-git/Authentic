package pw.iwmc.authentic.api.managers;

import pw.iwmc.authentic.api.account.AuthenticAccount;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public interface AuthenticAccountManager {
    Optional<AuthenticAccount> accountByName(String playerName);
    Optional<AuthenticAccount> accountById(UUID uniqueId);

    void addAccount(AuthenticAccount account);
    void removeAccount(AuthenticAccount account);
    void updateAccount(AuthenticAccount account);

    ConcurrentMap<String, UUID> cachedLicenses();
    ConcurrentMap<UUID, AuthenticAccount> cachedAccounts();
}
