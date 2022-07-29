package pw.iwmc.authentic.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.section.Section;

import noelle.encryptor.PasswordEncryptor;
import noelle.features.messages.common.AbstractMessages;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.managers.PluginAccountManager;
import pw.iwmc.authentic.managers.PluginStorageManager;
import pw.iwmc.authentic.messages.MessageKeys;

@Section(route = "unregister", aliases = {"unreg"})
public class UnregisterCommand {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();

    private final PluginAccountManager accountManager = authentic.accountManager();
    private final PluginStorageManager storageManager = authentic.storageManager();

    private final PasswordEncryptor encryptor = authentic.passwordEncryptor();
    private final AbstractMessages<Player> messages = authentic.messages();

    @Execute
    public void executeMain(CommandSource source, String[] args) {
        if (source instanceof Player player) {
            var accountOptional = accountManager.accountByName(player.getUsername());
            accountOptional.ifPresent(account -> {
                if (account.licensed()) {
                    var message = messages.message(MessageKeys.UNREGISTER_ACCOUNT_LICENSED);
                    player.sendMessage(message);
                    return;
                }

                if (args.length != 1) {
                    var message = messages.message(MessageKeys.UNREGISTER_USAGE);
                    player.sendMessage(message);
                    return;
                }

                var password = args[0];

                var hashedOld = encryptor.encode(password);
                var currentPassword = account.hashedPassword().get();

                if (!currentPassword.equals(hashedOld)) {
                    var message = messages.message(MessageKeys.UNREGISTER_WRONG_PASSWORD);
                    player.sendMessage(message);
                    return;
                }

                accountManager.removeAccount(account);
                storageManager.dropAccount(account);

                var defaultMessage = messages.message(MessageKeys.UNREGISTER_SUCCESS);
                player.disconnect(defaultMessage);
            });
        } else {
            var message = messages.message(MessageKeys.ONLY_FOR_PLAYERS);
            source.sendMessage(message);
        }
    }
}
