package pw.iwmc.authentic.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.section.Section;

import noelle.encryptor.PasswordEncryptor;
import noelle.features.messages.common.AbstractMessages;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.configuration.PluginConfiguration;
import pw.iwmc.authentic.managers.PluginAccountManager;
import pw.iwmc.authentic.managers.PluginStorageManager;
import pw.iwmc.authentic.messages.MessageKeys;

@Section(route = "changepassword", aliases = {"changepass", "cpass"})
public class ChangePasswordCommand {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();

    private final PluginConfiguration configuration = authentic.configuration();
    private final PluginAccountManager accountManager = authentic.accountManager();
    private final PluginStorageManager storageManager = authentic.storageManager();

    private final PasswordEncryptor encryptor = authentic.passwordEncryptor();
    private final AbstractMessages<Player> messages = authentic.messages();

    @Execute
    public void executeMain(CommandSource source, String[] args) {
        var messagesConfig = configuration.messagesConfiguration();

        if (source instanceof Player player) {
            var accountOptional = accountManager.accountByName(player.getUsername());
            accountOptional.ifPresent(account -> {
                if (account.licensed()) {
                    var message = messages.message(MessageKeys.CHANGEPASSWORD_ACCOUNT_LICENSED);
                    player.sendMessage(message);
                    return;
                }

                if (args.length != 2) {
                    var message = messages.message(MessageKeys.CHANGEPASSWORD_USAGE);
                    player.sendMessage(message);
                    return;
                }

                var oldPassword = args[0];
                var newPassword = args[1];

                var currentPassword = account.hashedPassword().get();

                if (!encryptor.matches(oldPassword, currentPassword)) {
                    var message = messages.message(MessageKeys.CHANGEPASSWORD_WRONG_OLD_PASSWORD);
                    player.sendMessage(message);
                    return;
                }

                var newHashed = encryptor.encode(newPassword);
                account.updateHashedPassword(newHashed);

                accountManager.updateAccount(account);
                storageManager.updateAccount(account);

                var defaultMessage = messages.message(MessageKeys.CHANGEPASSWORD_SUCCESS);
                player.sendMessage(defaultMessage);

                if (messagesConfig.hoversEnabled()) {
                    var hoverMessage = messages.message(MessageKeys.CHANGEPASSWORD_HOVER, "%password%", newPassword);
                    player.sendMessage(hoverMessage);
                }
            });
        } else {
            var message = messages.message(MessageKeys.ONLY_FOR_PLAYERS);
            source.sendMessage(message);
        }
    }
}
