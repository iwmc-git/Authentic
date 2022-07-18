package pw.iwmc.authentic.engine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.TaskStatus;

import org.jetbrains.annotations.NotNull;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.api.account.AuthenticAccount;
import pw.iwmc.authentic.api.engine.storage.AuthenticStorage;
import pw.iwmc.authentic.api.engine.AuthenticEngine;
import pw.iwmc.authentic.api.engine.login.LoginMode;
import pw.iwmc.authentic.api.engine.license.LicenseServerMode;
import pw.iwmc.authentic.configuration.PluginConfiguration;
import pw.iwmc.authentic.translation.TranslationKeys;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class PluginEngine implements AuthenticEngine {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();
    private final PluginConfiguration configuration = authentic.configuration();

    private final Cache<UUID, AuthenticAccount> cachedAccounts;

    private final Map<AuthenticAccount, ScheduledTask> authTimedTasks;
    private final Map<String, UUID> licenseIds;

    public PluginEngine() {
        this.authentic.debug("Loading engine...");
        this.cachedAccounts = Caffeine.newBuilder()
                .scheduler(Scheduler.systemScheduler())
                .build();

        this.authTimedTasks = new HashMap<>();
        this.licenseIds = new HashMap<>();
    }

    @Override
    public Map<UUID, AuthenticAccount> cachedAccounts() {
        return cachedAccounts.asMap();
    }

    @Override
    public Optional<AuthenticAccount> fromCache(UUID uniqueId) {
        return Optional.ofNullable(cachedAccounts.getIfPresent(uniqueId));
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

    public ScheduledTask defaultAuthTask(AuthenticAccount account) {
        return authentic.proxyServer().getScheduler().buildTask(authentic, () -> {
            var playerByName = authentic.proxyServer().getPlayer(account.playerName());
            var languages = authentic.languages();

            playerByName.ifPresent(player -> {
                var authorizeTime = account.lastConnectedDate().getTime();
                var currentTimeRemaining = authorizeTime - System.currentTimeMillis();

                if (account.registered()) {
                    var chatMessage = languages.translationFor(player, TranslationKeys.NEED_LOGIN_CHAT).translatedComponent();
                    player.sendMessage(chatMessage);
                } else {
                    var chatMessage = languages.translationFor(player, TranslationKeys.NEED_REGISTER_CHAT).translatedComponent();
                    player.sendMessage(chatMessage);
                }

                if (currentTimeRemaining < 0L) {
                    var timeOutMessage = languages.translationFor(player, TranslationKeys.TIME_OUT).translatedComponent();
                    var task = taskByAccount(account);

                    if (task.status() == TaskStatus.SCHEDULED) {
                        task.cancel();
                    }

                    player.disconnect(timeOutMessage);
                }
            });
        }).delay(1, TimeUnit.SECONDS).repeat(1, TimeUnit.SECONDS).schedule();
    }

    public ScheduledTask taskByAccount(AuthenticAccount account) {
        return authTimedTasks.get(account);
    }

    public void addInAuthTask(AuthenticAccount account) {
        authTimedTasks.put(account, defaultAuthTask(account));
    }

    public UUID licenseIdByName(String playerName) {
        return licenseIds.get(playerName);
    }

    public void addLicenseId(String playerName, UUID licenseId) {
        licenseIds.put(playerName, licenseId);
    }

    public Optional<Map.Entry<UUID, AuthenticAccount>> byName(String name) {
        var entries = cachedAccounts.asMap().entrySet();
        return entries.stream().filter(entry -> entry.getValue().playerName().equalsIgnoreCase(name)).findFirst();
    }

    public void uploadCache() {
        authentic.logger().info("Uploading cache data into database...");

        var storage = storage();
        var accounts = cachedAccounts.asMap().entrySet();

        if (accounts.isEmpty()) {
            authentic.debug("No account from cache was found!...");
            return;
        }

        accounts.forEach(entry -> {
            var account = entry.getValue();
            var accountFromStorage = storage.fromStorage(account.playerName());

            if (accountFromStorage != null) {
                authentic.debug(account.playerName() + "`s account exists in database! Updating...");
                storage.updateAccount(account);
            } else {
                authentic.debug(account.playerName() + "`s account does not exists in database! Creating new...");
                storage.makeAccount(account);
            }
        });

        authentic.debug("Uploaded " + accounts.size() + " accounts from cache!");
    }
}
