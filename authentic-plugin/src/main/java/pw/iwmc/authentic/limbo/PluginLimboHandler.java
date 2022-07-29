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
import pw.iwmc.authentic.limbo.commands.LimboCommands;
import pw.iwmc.authentic.limbo.enums.LimboCommand;
import pw.iwmc.authentic.managers.PluginAccountManager;
import pw.iwmc.authentic.messages.MessageKeys;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class PluginLimboHandler implements LimboSessionHandler {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();
    private final PluginConfiguration configuration = authentic.configuration();
    private final PluginAccountManager accountManager = authentic.accountManager();
    private final LimboCommands limboCommands = authentic.limboCommands();

    private final AbstractMessages<Player> messages = authentic.messages();

    private final AuthenticAccount account;
    private final Player player;
    private final BossBar bossBar;

    private ScheduledTask authTask;

    public PluginLimboHandler(AuthenticAccount account, Player player) {
        authentic.defaultLogger().info("Creating limbo handler for " + player.getUsername() + "...");

        this.account = account;
        this.player = player;

        var messagesConfig = configuration.messagesConfiguration();
        var bossbarColor = BossBar.Color.valueOf(messagesConfig.bossbarColor().name());
        var bossbarOverlay = BossBar.Overlay.valueOf(messagesConfig.bossbarOverlay().name());

        this.bossBar = BossBar.bossBar(Component.empty(), 1.0F, bossbarColor, bossbarOverlay);
    }

    @Override
    public void onSpawn(Limbo server, LimboPlayer limboPlayer) {
        limboPlayer.disableFalling();

        var scheduler = authentic.proxyServer().getScheduler();
        this.authTask = scheduler.buildTask(authentic, defaultAuthTask())
                .delay(0, TimeUnit.SECONDS)
                .repeat(1, TimeUnit.SECONDS)
                .schedule();

        var limboPlayers = accountManager.limboPlayers();
        limboPlayers.put(player.getUsername(), limboPlayer);

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
        var chatArgs = chat.split(" ");

        if (chatArgs.length == 0) {
            return;
        }

        switch (LimboCommand.parseCommand(chatArgs[0])) {
            case LOGIN -> limboCommands.loginCommand().execute(player, chatArgs);
            case REGISTER -> limboCommands.registerCommand().execute(player, chatArgs);
            case TOTP -> limboCommands.totpCommand().execute(player, chatArgs);
            case INVALID -> messages.sendMessage(player, MessageKeys.INVALID_COMMAND);
        }
    }

    @Override
    public void onDisconnect() {
        authentic.defaultLogger().info("Player " + player.getUsername() + " disconnected from limbo! Stooping tasks, hiding boss bar...");

        var limboPlayers = accountManager.limboPlayers();
        limboPlayers.remove(player.getUsername());

        if (authTask.status() == TaskStatus.SCHEDULED) {
            authTask.cancel();
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
                if (!account.passedLogin()) {
                    var message = messages.message(MessageKeys.LOGIN_REQUIRED_MESSAGE);
                    var bossbarMessage = messages.message(MessageKeys.LOGIN_BOSSBAR_REMAINING, "%time%", String.valueOf(remainAuthTime));

                    bossBar.name(bossbarMessage);
                    bossBar.progress(Math.min(1.0F, remainAuthTime * (1000.0F / authTime)));

                    if (cyclicMessages) {
                        player.sendMessage(message);
                    }

                    return;
                }

                if (account.hasTotp()) {
                    var message = messages.message(MessageKeys.TOTP_LIMBO_PASS_MESSAGE);
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
