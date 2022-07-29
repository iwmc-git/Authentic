package pw.iwmc.authentic.limbo.commands;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.title.Title;
import noelle.features.messages.common.AbstractMessages;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.configuration.PluginConfiguration;
import pw.iwmc.authentic.managers.PluginAccountManager;
import pw.iwmc.authentic.managers.PluginStorageManager;
import pw.iwmc.authentic.messages.MessageKeys;

import java.sql.Timestamp;
import java.time.Duration;

public class LoginCommand {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();
    private final PluginConfiguration configuration = authentic.configuration();
    private final PluginStorageManager storageManager = authentic.storageManager();
    private final PluginAccountManager accountManager = authentic.accountManager();

    private final AbstractMessages<Player> messages = authentic.messages();

    public void execute(Player player, String[] arguments) {
        authentic.debug("Executing login command for " + player.getUsername());

        var messagesConfig = configuration.messagesConfiguration();
        var mainConfig = configuration.mainConfiguration();

        var accountOptional = accountManager.accountByName(player.getUsername());
        var limboPlayer = accountManager.limboPlayers().get(player.getUsername());

        if (accountOptional.isEmpty()) {
            return;
        }

        var account = accountOptional.get();

        if (!account.registered()) {
            messages.sendMessage(player, MessageKeys.LOGIN_NOT_REGISTERED);
            return;
        }

        if (arguments.length != 2) {
            messages.sendMessage(player, MessageKeys.LOGIN_USAGE);
            return;
        }

        var hashedPassword = authentic.passwordEncryptor().encode(arguments[1]);
        var currentPassword = account.hashedPassword();

        if (currentPassword.isPresent() && currentPassword.get().equalsIgnoreCase(hashedPassword)) {
            authentic.debug("Handling account login for " + account.playerName() + "...");

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

            player.clearTitle();

            var totpConfig = configuration.totpConfiguration();
            if (totpConfig.totpEnabled() && account.hasTotp()) {
                account.passLogin(true);

                var message = messages.message(MessageKeys.TOTP_LIMBO_PASS_MESSAGE);
                player.sendMessage(message);

                if (messagesConfig.titlesEnabled()) {
                    var totpTitleSettings = messagesConfig.totpTitleSettings();

                    var titleMessage = messages.message(MessageKeys.TOTP_LIMBO_PASS_TITLE);
                    var subtitleMessage = messages.message(MessageKeys.TOTP_LIMBO_PASS_SUBTITLE);

                    var fadeIn = Duration.ofMillis(totpTitleSettings.fadeIn());
                    var stay = Duration.ofMillis(totpTitleSettings.stay());
                    var fadeOut = Duration.ofMillis(totpTitleSettings.fadeOut());

                    var times = Title.Times.times(fadeIn, stay, fadeOut);
                    var title = Title.title(titleMessage, subtitleMessage, times);

                    player.showTitle(title);
                }
            } else {
                var endSessionTime = new Timestamp(System.currentTimeMillis() + (mainConfig.sessionTime() * 60000));
                var address = player.getRemoteAddress().getAddress();

                account.updateSessionEndDate(endSessionTime);
                account.updateLastLoggedAddress(address);

                accountManager.updateAccount(account);
                storageManager.updateAccount(account);

                limboPlayer.disconnect();
            }
        } else {
            messages.sendMessage(player, MessageKeys.LOGIN_WRONG_PASSWORD);
        }
    }
}
