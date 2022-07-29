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

public class RegisterCommand {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();
    private final PluginConfiguration configuration = authentic.configuration();
    private final PluginStorageManager storageManager = authentic.storageManager();
    private final PluginAccountManager accountManager = authentic.accountManager();

    private final AbstractMessages<Player> messages = authentic.messages();

    public void execute(Player player, String[] arguments) {
        authentic.debug("Executing register command for " + player.getUsername());

        var messagesConfig = configuration.messagesConfiguration();
        var securityConfig = configuration.securityConfiguration();
        var mainConfig = configuration.mainConfiguration();

        var accountOptional = accountManager.accountByName(player.getUsername());
        var limboPlayer = accountManager.limboPlayers().get(player.getUsername());

        if (accountOptional.isEmpty()) {
            return;
        }

        var account = accountOptional.get();

        if (account.registered()) {
            messages.sendMessage(player, MessageKeys.REGISTER_ALREADY);
            return;
        }

        var minLenght = securityConfig.minPasswordLength();
        var maxLenght = securityConfig.maxPasswordLength();

        if (arguments.length != 2) {
            messages.sendMessage(player, MessageKeys.REGISTER_USAGE);
            return;
        }

        var startPassword = arguments[1];

        if (startPassword.length() > maxLenght) {
            var message = messages.message(MessageKeys.PASSWORD_TOO_LONG);
            player.sendMessage(message);
            return;
        }

        if (startPassword.length() < minLenght) {
            var message = messages.message(MessageKeys.PASSWORD_TOO_SHORT);
            player.sendMessage(message);
            return;
        }

        var unsafePasswords = authentic.unsafePasswords();
        var checkPasswordStrength = configuration.securityConfiguration().checkPasswordStrength();

        if (checkPasswordStrength && unsafePasswords.contains(startPassword)) {
            var message = messages.message(MessageKeys.UNSAFE_PASSWORD);
            player.sendMessage(message);
        }

        authentic.debug("Handling account register for " + account.playerName() + "...");
        var hashedPassword = authentic.passwordEncryptor().encode(startPassword);

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

            if (messagesConfig.hoversEnabled()) {
                var hoverMessage = messages.message(MessageKeys.REGISTER_SUCCESS_HOVER, "%password%", startPassword);
                player.sendMessage(hoverMessage);
            }
        });

        accountManager.updateAccount(account);
        storageManager.updateAccount(account);

        player.clearTitle();
        limboPlayer.disconnect();
    }
}
