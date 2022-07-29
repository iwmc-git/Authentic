package pw.iwmc.authentic.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.section.Section;

import net.kyori.adventure.text.Component;
import noelle.encryptor.PasswordEncryptor;
import noelle.features.messages.common.AbstractMessages;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.configuration.PluginConfiguration;
import pw.iwmc.authentic.managers.PluginAccountManager;
import pw.iwmc.authentic.managers.PluginLicenseManager;
import pw.iwmc.authentic.managers.PluginStorageManager;
import pw.iwmc.authentic.messages.MessageKeys;

import java.sql.Timestamp;

@Section(route = "license", aliases = {"premium"})
public class LicenseCommand {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();

    private final PluginConfiguration configuration = authentic.configuration();
    private final PluginAccountManager accountManager = authentic.accountManager();
    private final PluginStorageManager storageManager = authentic.storageManager();
    private final PluginLicenseManager licenseManager = authentic.licenseManager();

    private final PasswordEncryptor encryptor = authentic.passwordEncryptor();
    private final AbstractMessages<Player> messages = authentic.messages();

    @Execute
    public void executeMain(CommandSource source) {
        if (source instanceof Player player) {
            var accountOptional = accountManager.accountByName(player.getUsername());
            accountOptional.ifPresent(account -> {
                var message = messages.message(MessageKeys.LICENSE_COMMON_USAGE);
                player.sendMessage(message);
            });
        } else {
            var message = messages.message(MessageKeys.ONLY_FOR_PLAYERS);
            source.sendMessage(message);
        }
    }

    @Execute(route = "apply")
    public void executeApply(CommandSource source, String[] args) {
        if (source instanceof Player player) {
            var accountOptional = accountManager.accountByName(player.getUsername());
            accountOptional.ifPresent(account -> {
                if (args.length != 1) {
                    var message = messages.message(MessageKeys.LICENSE_APPLY_USAGE);
                    player.sendMessage(message);
                    return;
                }

                if (account.licensed()) {
                    var message = messages.message(MessageKeys.LICENSE_ALREADY);
                    player.sendMessage(message);
                    return;
                }

                var licenseId = licenseManager.retrieveFor(player.getUsername());
                if (licenseId == null) {
                    var message = messages.message(MessageKeys.LICENSE_NOT_FOUND);
                    player.sendMessage(message);
                    return;
                }

                account.updateLicenseId(licenseId);
                account.updateLastLoggedAddress(null);
                account.updateSessionEndDate(null);

                accountManager.updateAccount(account);
                storageManager.updateAccount(account);

                var message = messages.message(MessageKeys.LICENSE_APPLIED);
                player.disconnect(message);
            });
        } else {
            var message = messages.message(MessageKeys.ONLY_FOR_PLAYERS);
            source.sendMessage(message);
        }
    }

    @Execute(route = "discard")
    public void executeDiscard(CommandSource source, String[] args) {
        if (source instanceof Player player) {
            var mainConfig = configuration.mainConfiguration();
            var accountOptional = accountManager.accountByName(player.getUsername());
            accountOptional.ifPresent(account -> {
                if (args.length != 2) {
                    var message = messages.message(MessageKeys.LICENSE_DISCARD_USAGE);
                    player.sendMessage(message);
                    return;
                }

                if (!account.licensed()) {
                    var message = messages.message(MessageKeys.LICENSE_NOT_APPLIED);
                    player.sendMessage(message);
                    return;
                }

                var unsafePasswords = authentic.unsafePasswords();
                var checkPasswordStrength = configuration.securityConfiguration().checkPasswordStrength();

                var password = args[1];

                if (checkPasswordStrength && unsafePasswords.contains(password)) {
                    var message = messages.message(MessageKeys.UNSAFE_PASSWORD);
                    player.sendMessage(message);
                }

                var hashedPassword = encryptor.encode(password);

                var endSessionTime = new Timestamp(System.currentTimeMillis() + (mainConfig.sessionTime() * 60000));
                var address = player.getRemoteAddress().getAddress();

                account.updateHashedPassword(hashedPassword);
                account.updateSessionEndDate(endSessionTime);
                account.updateLastLoggedAddress(address);
                account.updateLicenseId(null);

                accountManager.updateAccount(account);
                storageManager.updateAccount(account);

                var message = messages.message(MessageKeys.LICENSE_DISCARDED);
                player.disconnect(message);
            });
        } else {
            var message = messages.message(MessageKeys.ONLY_FOR_PLAYERS);
            source.sendMessage(message);
        }
    }
}
