package pw.iwmc.authentic.api.managers;

import pw.iwmc.authentic.api.account.AuthenticAccount;

import java.util.Optional;
import java.util.UUID;

public interface AuthenticStorageManager {
    Optional<AuthenticAccount> accountByName(String name);
    Optional<AuthenticAccount> accountById(UUID id);

    void insertAccount(AuthenticAccount account);
    void updateAccount(AuthenticAccount account);
    void dropAccount(AuthenticAccount account);
}
