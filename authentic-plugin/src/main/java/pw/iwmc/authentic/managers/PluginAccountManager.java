package pw.iwmc.authentic.managers;

import com.velocitypowered.api.proxy.Player;
import net.elytrium.limboapi.api.player.LimboPlayer;
import noelle.features.messages.common.AbstractMessages;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.api.account.AuthenticAccount;
import pw.iwmc.authentic.api.managers.AuthenticAccountManager;
import pw.iwmc.authentic.configuration.PluginConfiguration;
import pw.iwmc.authentic.limbo.PluginLimbo;
import pw.iwmc.authentic.limbo.PluginLimboHandler;
import pw.iwmc.authentic.messages.MessageKeys;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PluginAccountManager implements AuthenticAccountManager {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();

    private final AbstractMessages<Player> messages = authentic.messages();

    private final PluginConfiguration configuration = authentic.configuration();
    private final PluginLimbo limbo = authentic.limbo();

    private final ConcurrentMap<UUID, AuthenticAccount> cachedAccounts;
    private final ConcurrentMap<String, UUID> cachedLicenses;

    private final ConcurrentMap<String, Runnable> postLoginTasks;
    private final ConcurrentMap<String, Runnable> postRegisterTasks;

    private final ConcurrentMap<String, LimboPlayer> limboPlayers;

    public PluginAccountManager() {
        authentic.defaultLogger().info("Loading account manager...");

        this.cachedAccounts = new ConcurrentHashMap<>();
        this.cachedLicenses = new ConcurrentHashMap<>();

        this.postLoginTasks = new ConcurrentHashMap<>();
        this.postRegisterTasks = new ConcurrentHashMap<>();

        this.limboPlayers = new ConcurrentHashMap<>();

        authentic.defaultLogger().info("All cached maps are loaded!");
    }

    public void authorize(Player player, AuthenticAccount account) {
        var nameRegex = configuration.securityConfiguration().nameRegex();

        if (nameRegex.matcher(player.getUsername()).matches()) {
            limbo.spawnInLimbo(player, new PluginLimboHandler(account, player));
        } else {
            var message = messages.message(MessageKeys.INVALID_NAME);
            player.disconnect(message);
        }
    }

    public ConcurrentMap<String, Runnable> postLoginTasks() {
        return postLoginTasks;
    }

    public ConcurrentMap<String, Runnable> postRegisterTasks() {
        return postRegisterTasks;
    }

    public ConcurrentMap<String, LimboPlayer> limboPlayers() {
        return limboPlayers;
    }

    @Override
    public Optional<AuthenticAccount> accountByName(String playerName) {
        authentic.debug("Finding account with name " + playerName + " in cache...");

        var accountEntry = cachedAccounts.entrySet()
                .stream()
                .filter(entry -> entry.getValue().playerName().equalsIgnoreCase(playerName))
                .findFirst();

        if (accountEntry.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(accountEntry.get().getValue());
        }
    }

    @Override
    public Optional<AuthenticAccount> accountById(UUID uniqueId) {
        authentic.debug("Finding account with id " + uniqueId.toString() + " in cache...");
        return Optional.ofNullable(cachedAccounts.get(uniqueId));
    }

    @Override
    public void addAccount(AuthenticAccount account) {
        authentic.debug("Adding " + account.playerName() + "`s account in cache...");

        if (cachedAccounts.get(account.playerUniqueId()) == null) {
            authentic.debug(account.playerName() + "`s not exists in cache! Adding...");
            cachedAccounts.put(account.playerUniqueId(), account);
        }
    }

    @Override
    public void removeAccount(AuthenticAccount account) {
        authentic.debug("Deleting " + account.playerName() + "`s account in cache...");

        if (cachedAccounts.get(account.playerUniqueId()) != null) {
            authentic.debug(account.playerName() + "`s exists in cache! Removing...");
            cachedAccounts.remove(account.playerUniqueId(), account);
        }
    }

    @Override
    public void updateAccount(AuthenticAccount account) {
        authentic.debug("Updating " + account.playerName() + "`s account in cache...");

        if (cachedAccounts.get(account.playerUniqueId()) != null) {
            authentic.debug(account.playerName() + "`s exists in cache! Removing...");
            cachedAccounts.remove(account.playerUniqueId(), account);
        }

        cachedAccounts.put(account.playerUniqueId(), account);
    }

    @Override
    public ConcurrentMap<String, UUID> cachedLicenses() {
        return cachedLicenses;
    }

    @Override
    public ConcurrentMap<UUID, AuthenticAccount> cachedAccounts() {
        return cachedAccounts;
    }
}
