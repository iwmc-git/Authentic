package pw.iwmc.authentic.limbo;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.TaskStatus;

import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.player.LimboPlayer;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import noelle.features.messages.common.AbstractMessages;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.api.account.AuthenticAccount;
import pw.iwmc.authentic.configuration.PluginConfiguration;
import pw.iwmc.authentic.managers.PluginAccountManager;
import pw.iwmc.authentic.managers.PluginStorageManager;
import pw.iwmc.authentic.messages.MessageKeys;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class PluginLimboHandler implements LimboSessionHandler {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();
    private final PluginConfiguration configuration = authentic.configuration();
    private final PluginStorageManager storageManager = authentic.storageManager();
    private final PluginAccountManager accountManager = authentic.accountManager();

    private final AbstractMessages<Player> messages = authentic.messages();

    private final AuthenticAccount account;
    private final Player player;
    private final BossBar bossBar;

    private ScheduledTask scheduledTask;
    private LimboPlayer limboPlayer;

    public PluginLimboHandler(AuthenticAccount account, Player player) {
        this.account = account;
        this.player = player;

        var messagesConfig = configuration.messagesConfiguration();
        var bossbarColor = BossBar.Color.valueOf(messagesConfig.bossbarColor().name());
        var bossbarOverlay = BossBar.Overlay.valueOf(messagesConfig.bossbarOverlay().name());

        this.bossBar = BossBar.bossBar(Component.empty(), 1.0F, bossbarColor, bossbarOverlay);
        authentic.defaultLogger().info("Creating limbo handler for " + player.getUsername() + "...");
    }

    @Override
    public void onSpawn(Limbo server, LimboPlayer limboPlayer) {
        this.limboPlayer = limboPlayer;
        limboPlayer.disableFalling();

        var scheduler = authentic.proxyServer().getScheduler();
        this.scheduledTask = scheduler.buildTask(authentic, defaultAuthTask())
                .delay(0, TimeUnit.SECONDS)
                .repeat(1, TimeUnit.SECONDS)
                .schedule();

        var messagesConfig = configuration.messagesConfiguration();

        var registerRequired = messagesConfig.requiredRegisterTitleSettings();
        var loginRequired = messagesConfig.requiredLoginTitleSettings();
        var titlesEnabled = messagesConfig.titlesEnabled();
        var bossbarEnabled = messagesConfig.bossbarEnabled();

        if (bossbarEnabled) {
            player.showBossBar(bossBar);
        }

        if (titlesEnabled) {
            if (!account.registered()) {
                var titleMessage = messages.message(MessageKeys.REGISTER_REQUIRED_TITLE);
                var subtitleMessage = messages.message(MessageKeys.REGISTER_REQUIRED_SUBTITLE);

                var fadeIn = Duration.ofMillis(registerRequired.fadeIn());
                var stay = Duration.ofMillis(registerRequired.stay());
                var fadeOut = Duration.ofMillis(registerRequired.fadeOut());

                var times = Title.Times.times(fadeIn, stay, fadeOut);
                var title = Title.title(titleMessage, subtitleMessage, times);

                var message = messages.message(MessageKeys.REGISTER_REQUIRED_MESSAGE);
                player.sendMessage(message);

                player.showTitle(title);
                return;
            }

            if (!account.logged()) {
                var titleMessage = messages.message(MessageKeys.LOGIN_REQUIRED_TITLE);
                var subtitleMessage = messages.message(MessageKeys.LOGIN_REQUIRED_SUBTITLE);

                var fadeIn = Duration.ofMillis(loginRequired.fadeIn());
                var stay = Duration.ofMillis(loginRequired.stay());
                var fadeOut = Duration.ofMillis(loginRequired.fadeOut());

                var times = Title.Times.times(fadeIn, stay, fadeOut);
                var title = Title.title(titleMessage, subtitleMessage, times);

                var message = messages.message(MessageKeys.LOGIN_REQUIRED_MESSAGE);
                player.sendMessage(message);

                player.showTitle(title);
            }
        }
    }

    @Override
    public void onChat(String chat) {
        if (chat.startsWith("/")) {
            var message = messages.message(MessageKeys.SLASH_FIRST);
            player.sendMessage(message);
            return;
        }

        var messagesConfig = configuration.messagesConfiguration();
        var mainConfig = configuration.mainConfiguration();

        var unsafePasswords = authentic.unsafePasswords();
        var checkPasswordStrength = configuration.securityConfiguration().checkPasswordStrength();

        if (checkPasswordStrength && unsafePasswords.contains(chat.split(" ")[0])) {
            var message = messages.message(MessageKeys.UNSAFE_PASSWORD);
            player.sendMessage(message);
        }

        if (!account.registered()) {
            var securityConfig = configuration.securityConfiguration();

            var minLenght = securityConfig.minPasswordLength();
            var maxLenght = securityConfig.maxPasswordLength();

            if (chat.length() > maxLenght) {
                var message = messages.message(MessageKeys.PASSWORD_TOO_LONG);
                player.sendMessage(message);
                return;
            }

            if (chat.length() < minLenght) {
                var message = messages.message(MessageKeys.PASSWORD_TOO_SHORT);
                player.sendMessage(message);
                return;
            }

            if (mainConfig.registerNeedRepeatPassword()) {
                var password = chat.split(" ");

                if (password.length == 1) {
                    var message = messages.message(MessageKeys.REGISTER_NEED_REPEAT_MESSAGE);
                    player.sendMessage(message);
                    return;
                }

                if (password.length == 2) {
                    var startPassword = password[0];
                    var endPassword = password[1];

                    if (!endPassword.equals(startPassword)) {
                        var message = messages.message(MessageKeys.REGISTER_REPEAT_PASSWORD_NOT_MATCH_MESSAGE);
                        player.sendMessage(message);
                        return;
                    }
                }
            }
            authentic.defaultLogger().info("Handling account register for " + account.playerName() + "...");
            var hashedPassword = authentic.passwordEncryptor().encode(chat.split(" ")[0]);

            var endSessionTime = new Timestamp(System.currentTimeMillis() + (mainConfig.sessionTime() * 60000));
            var address = player.getRemoteAddress().getAddress();

            account.updateHashedPassword(hashedPassword);
            account.updateSessionEndDate(endSessionTime);
            account.updateLastLoggedAddress(address);

            var postRegisterTasks = authentic.accountManager().postRegisterTasks();
            postRegisterTasks.put(player.getUsername(), () -> {
                var message = messages.message(MessageKeys.REGISTER_SUCCESS_MESSAGE);
                player.sendMessage(message);

                if (messagesConfig.titlesEnabled()) {
                    var registerSuccess = messagesConfig.registeredTitleSettings();

                    var titleMessage = messages.message(MessageKeys.REGISTER_SUCCESS_TITLE);
                    var subtitleMessage = messages.message(MessageKeys.REGISTER_SUCCESS_SUBTITLE);

                    var fadeIn = Duration.ofMillis(registerSuccess.fadeIn());
                    var stay = Duration.ofMillis(registerSuccess.stay());
                    var fadeOut = Duration.ofMillis(registerSuccess.fadeOut());

                    var times = Title.Times.times(fadeIn, stay, fadeOut);
                    var title = Title.title(titleMessage, subtitleMessage, times);

                    player.showTitle(title);
                }
            });

            accountManager.updateAccount(account);
            storageManager.updateAccount(account);

            player.clearTitle();
            limboPlayer.disconnect();
        } else {
            if (!account.logged()) {
                authentic.defaultLogger().info("Handling account login for " + account.playerName() + "...");

                var hashedPassword = authentic.passwordEncryptor().encode(chat.split(" ")[0]);
                var currentPassword = account.hashedPassword();

                if (currentPassword.isPresent() && currentPassword.get().equalsIgnoreCase(hashedPassword)) {
                    var endSessionTime = new Timestamp(System.currentTimeMillis() + (mainConfig.sessionTime() * 60000));
                    var address = player.getRemoteAddress().getAddress();

                    account.updateSessionEndDate(endSessionTime);
                    account.updateLastLoggedAddress(address);

                    var postLoginTasks = authentic.accountManager().postLoginTasks();
                    postLoginTasks.put(player.getUsername(), () -> {
                        var message = messages.message(MessageKeys.LOGIN_SUCCESS_MESSAGE);
                        player.sendMessage(message);

                        if (messagesConfig.titlesEnabled()) {
                            var registerSuccess = messagesConfig.loggedTitleSettings();

                            var titleMessage = messages.message(MessageKeys.LOGIN_SUCCESS_TITLE);
                            var subtitleMessage = messages.message(MessageKeys.LOGIN_SUCCESS_SUBTITLE);

                            var fadeIn = Duration.ofMillis(registerSuccess.fadeIn());
                            var stay = Duration.ofMillis(registerSuccess.stay());
                            var fadeOut = Duration.ofMillis(registerSuccess.fadeOut());

                            var times = Title.Times.times(fadeIn, stay, fadeOut);
                            var title = Title.title(titleMessage, subtitleMessage, times);

                            player.showTitle(title);
                        }
                    });

                    accountManager.updateAccount(account);
                    storageManager.updateAccount(account);

                    player.clearTitle();
                    limboPlayer.disconnect();
                } else {
                    var message = messages.message(MessageKeys.UNKNOWN_PASSWORD);
                    player.sendMessage(message);
                }
            }
        }
    }

    @Override
    public void onDisconnect() {
        authentic.defaultLogger().info("Player " + player.getUsername() + " disconnected from limbo! Stooping tasks, hiding boss bar...");

        if (scheduledTask.status() == TaskStatus.SCHEDULED) {
            scheduledTask.cancel();
        }

        player.hideBossBar(bossBar);
    }

    private Runnable defaultAuthTask() {
        var authTime = configuration.mainConfiguration().authorizeTime();
        var cyclicMessages = configuration.messagesConfiguration().cyclicMessagesEnabled();

        return () -> {
            var accountConnectionTime = account.lastConnectedDate().getTime() + authTime;
            var remainAuthTime = (accountConnectionTime - System.currentTimeMillis()) / 1000;

            if (!account.registered()) {
                var message = messages.message(MessageKeys.REGISTER_REQUIRED_MESSAGE);
                var bossbarMessage = messages.message(MessageKeys.REGISTER_BOSSBAR_REMAINING, "%time%", String.valueOf(remainAuthTime));

                bossBar.name(bossbarMessage);
                bossBar.progress(Math.min(1.0F, remainAuthTime * (1000.0F / authTime)));

                if (cyclicMessages) {
                    player.sendMessage(message);
                }
            } else {
                if (!account.logged()) {
                    var message = messages.message(MessageKeys.LOGIN_REQUIRED_MESSAGE);
                    var bossbarMessage = messages.message(MessageKeys.LOGIN_BOSSBAR_REMAINING, "%time%", String.valueOf(remainAuthTime));

                    bossBar.name(bossbarMessage);
                    bossBar.progress(Math.min(1.0F, remainAuthTime * (1000.0F / authTime)));

                    if (cyclicMessages) {
                        player.sendMessage(message);
                    }
                }
            }

            if (remainAuthTime <= 0) {
                var message = messages.message(MessageKeys.TIME_OUT);
                player.disconnect(message);
            }
        };
    }
}
