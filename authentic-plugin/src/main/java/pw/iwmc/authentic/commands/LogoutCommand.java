package pw.iwmc.authentic.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.section.Section;
import noelle.features.messages.common.AbstractMessages;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.managers.PluginAccountManager;
import pw.iwmc.authentic.managers.PluginStorageManager;
import pw.iwmc.authentic.messages.MessageKeys;

@Section(route = "logout")
public class LogoutCommand {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();

    private final PluginAccountManager accountManager = authentic.accountManager();
    private final PluginStorageManager storageManager = authentic.storageManager();

    private final AbstractMessages<Player> messages = authentic.messages();

    @Execute
    public void executeMain(CommandSource source) {
        if (source instanceof Player player) {
            var accountOptional = accountManager.accountByName(player.getUsername());
            accountOptional.ifPresent(account -> {
                if (account.licensed()) {
                    var message = messages.message(MessageKeys.LOGOUT_ACCOUNT_LICENSED);
                    player.sendMessage(message);
                    return;
                }

                account.updateLastLoggedAddress(null);
                account.updateSessionEndDate(null);

                accountManager.updateAccount(account);
                storageManager.updateAccount(account);

                var message = messages.message(MessageKeys.LOGOUT_SUCCESS);
                player.disconnect(message);
            });
        } else {
            var message = messages.message(MessageKeys.ONLY_FOR_PLAYERS);
            source.sendMessage(message);
        }
    }
}