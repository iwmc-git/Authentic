package pw.iwmc.authentic.engine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;

import org.jetbrains.annotations.NotNull;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.api.account.AuthenticAccount;
import pw.iwmc.authentic.api.engine.storage.AuthenticStorage;
import pw.iwmc.authentic.api.engine.AuthenticEngine;
import pw.iwmc.authentic.api.engine.login.LoginMode;
import pw.iwmc.authentic.api.engine.license.LicenseServerMode;
import pw.iwmc.authentic.configuration.PluginConfiguration;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PluginEngine implements AuthenticEngine {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();
    private final PluginConfiguration configuration;

    private final Cache<UUID, AuthenticAccount> cachedAccounts;

    public PluginEngine() {
        this.authentic.logger().info("Loading engine...");
        this.configuration = this.authentic.configuration();

        this.authentic.logger().info("Loading cache...");
        this.cachedAccounts = Caffeine.newBuilder()
                .scheduler(Scheduler.systemScheduler())
                .build();
    }

    @Override
    public Map<UUID, AuthenticAccount> cachedAccounts() {
        return cachedAccounts.asMap();
    }

    @Override
    public Optional<AuthenticAccount> fromCache(UUID uniqueId) {
        return Optional.ofNullable(cachedAccounts.getIfPresent(uniqueId));
    }

    public Optional<Map.Entry<UUID, AuthenticAccount>> byName(String name) {
        var entries = cachedAccounts.asMap().entrySet();
        return entries.stream().filter(entry -> entry.getValue().playerName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public AuthenticStorage storage() {
        return authentic.storage();
    }

    @Override
    public LoginMode currentLoginMode() {
        return configuration.loginMode();
    }

    @Override
    public LicenseServerMode licenseServerMode() {
        return configuration.licenseServerMode();
    }

    @Override
    public void addCachedAccount(@NotNull AuthenticAccount account) {
        this.removeCachedAccount(account);

        cachedAccounts.put(account.playerUniqueId(), account);
    }

    @Override
    public void removeCachedAccount(@NotNull AuthenticAccount account) {
        var optionalAuthenticAccount = this.fromCache(account.playerUniqueId());
        optionalAuthenticAccount.ifPresent(authenticAccount -> cachedAccounts.invalidate(account.playerUniqueId()));
    }

    @Override
    public void handleAccountLogin(@NotNull AuthenticAccount account) {
        this.authentic.logger().info("Executing account login for " + account.playerName() + "...");
    }

    @Override
    public void handleAccountRegister(@NotNull AuthenticAccount account) {
        this.authentic.logger().info("Executing account register for " + account.playerName() + "...");

    }

    @Override
    public void handleAccountLogout(@NotNull AuthenticAccount account) {
        this.authentic.logger().info("Executing account logout for " + account.playerName() + "...");

    }

    @Override
    public void handleAccountLicenseState(@NotNull AuthenticAccount account) {
        var state = account.licensed() ? "LICENSED" : "NOT LICENSED";
        this.authentic.logger().info("Executing account login for " + account.playerName() + "... (State: " + state + ")");

    }

    @Override
    public void handleAccountChangePassword(@NotNull AuthenticAccount account) {
        this.authentic.logger().info("Executing account password changing for " + account.playerName() + "...");

    }

    @Override
    public void handleAccountUnregister(@NotNull AuthenticAccount account) {
        this.authentic.logger().info("Executing account unregister for " + account.playerName() + "...");

    }

    @Override
    public void dropAccount(@NotNull AuthenticAccount account, boolean alsoForStorage) {
        this.removeCachedAccount(account);

        if (alsoForStorage) {
            authentic.storage().dropAccount(account);
        }
    }

    @Override
    public void truncateAccount(@NotNull AuthenticAccount account, boolean alsoForStorage) {
        this.addCachedAccount(account);

        if (alsoForStorage) {
            authentic.storage().truncateAccount(account);
        }
    }

    @Override
    public void makeAccount(@NotNull AuthenticAccount account, boolean alsoForStorage) {
        this.addCachedAccount(account);

        if (alsoForStorage) {
            authentic.storage().truncateAccount(account);
        }
    }

    public void uploadCache() {
        authentic.logger().info("Uploading cache data into database...");
        var accounts = new ArrayList<>(cachedAccounts.asMap().entrySet());

        if (accounts.isEmpty()) {
            authentic.logger().info("No account from cache was found!...");
            return;
        }

        var storage = storage();
        accounts.forEach(entry -> {
            var account = entry.getValue();

            if (storage.fromStorage(account.playerName()) != null) {
                authentic.logger().info("Account " + account.playerName() + " exists in database! Rewriting...");
                storage.updateAccount(account);
            } else {
                authentic.logger().info("Account " + account.playerName() + " not exists! Creating new account...");
                authentic.storage().makeAccount(account);
            }
        });

        authentic.logger().info("Uploaded " + accounts.size() + " accounts from cache!");
    }
}
