package pw.iwmc.authentic.api.engine.storage;

import org.jetbrains.annotations.Nullable;
import pw.iwmc.authentic.api.account.AuthenticAccount;

import java.util.UUID;

public interface AuthenticStorage {
    @Nullable AuthenticAccount fromStorage(String name);
    @Nullable AuthenticAccount fromStorage(UUID uniqueId);

    StorageType storageType();

    void dropAccount(AuthenticAccount account);
    void truncateAccount(AuthenticAccount account);
    void updateAccount(AuthenticAccount account);
    void makeAccount(AuthenticAccount account);
}
