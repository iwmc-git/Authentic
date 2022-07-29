package pw.iwmc.authentic.limbo.commands;

import com.velocitypowered.api.proxy.Player;
import noelle.features.messages.common.AbstractMessages;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.configuration.PluginConfiguration;
import pw.iwmc.authentic.managers.PluginAccountManager;
import pw.iwmc.authentic.managers.PluginStorageManager;
import pw.iwmc.authentic.managers.PluginTotpManager;
import pw.iwmc.authentic.messages.MessageKeys;

import java.sql.Timestamp;

public class TotpCommand {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();
    private final PluginAccountManager accountManager = authentic.accountManager();
    private final PluginTotpManager totpManager = authentic.totpManager();
    private final PluginConfiguration configuration = authentic.configuration();
    private final PluginStorageManager storageManager = authentic.storageManager();

    private final AbstractMessages<Player> messages = authentic.messages();

    public void execute(Player player, String[] arguments) {
        authentic.debug("Executing totp command for " + player.getUsername());

        var accountOptional = accountManager.accountByName(player.getUsername());
        var limboPlayer = accountManager.limboPlayers().get(player.getUsername());

        if (accountOptional.isEmpty()) {
            return;
        }

        var account = accountOptional.get();

        if (arguments.length != 2) {
            messages.sendMessage(player, MessageKeys.TOTP_LIMBO_USAGE);
            return;
        }

        if (!account.passedLogin()) {
            messages.sendMessage(player, MessageKeys.TOTP_LIMBO_NOT_LOGGED);
            return;
        }

        if (!account.registered()) {
            messages.sendMessage(player, MessageKeys.TOTP_LIMBO_NOT_REGISTERED);
            return;
        }

        if (account.totpToken().isEmpty() && !account.hasTotp()) {
            messages.sendMessage(player, MessageKeys.TOTP_LIMBO_NOT_FOUND);
            return;
        }

        var token = account.totpToken().get();
        var totpKey = arguments[1];

        authentic.debug("Handling account totp verify for " + account.playerName() + "...");

        if (!totpManager.codeVerifier().isValidCode(token, totpKey)) {
            var message = messages.message(MessageKeys.TOTP_LIMBO_WRONG);
            player.sendMessage(message);
        } else {

            var mainConfig = configuration.mainConfiguration();

            var endSessionTime = new Timestamp(System.currentTimeMillis() + (mainConfig.sessionTime() * 60000));
            var address = player.getRemoteAddress().getAddress();

            account.updateSessionEndDate(endSessionTime);
            account.updateLastLoggedAddress(address);

            accountManager.updateAccount(account);
            storageManager.updateAccount(account);

            var message = messages.message(MessageKeys.TOTP_LIMBO_PASSED);
            player.sendMessage(message);
            limboPlayer.disconnect();
        }
    }
}
