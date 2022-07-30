package pw.iwmc.authentic.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.section.Section;

import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;

import noelle.encryptor.PasswordEncryptor;
import noelle.features.messages.common.AbstractMessages;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.configuration.PluginConfiguration;
import pw.iwmc.authentic.managers.PluginAccountManager;
import pw.iwmc.authentic.managers.PluginStorageManager;
import pw.iwmc.authentic.managers.PluginTotpManager;
import pw.iwmc.authentic.messages.MessageKeys;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Section(route = "totp" , aliases = {"2fa", "2factor"})
public class TotpCommand {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();

    private final PluginConfiguration configuration = authentic.configuration();
    private final PluginAccountManager accountManager = authentic.accountManager();
    private final PluginStorageManager storageManager = authentic.storageManager();
    private final PluginTotpManager totpManager = authentic.totpManager();

    private final PasswordEncryptor encryptor = authentic.passwordEncryptor();
    private final AbstractMessages<Player> messages = authentic.messages();

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final RecoveryCodeGenerator codesGenerator = new RecoveryCodeGenerator();

    @Execute
    public void executeMain(CommandSource source, String[] args) {
        if (source instanceof Player player) {
            var accountOptional = accountManager.accountByName(player.getUsername());
            accountOptional.ifPresent(account -> {
                var totpEnabled = configuration.totpConfiguration().totpEnabled();
                if (!totpEnabled) {
                    var message = messages.message(MessageKeys.TOTP_FORCE_DISABLED);
                    player.sendMessage(message);
                    return;
                }

                if (account.licensed()) {
                    messages.sendMessage(player, MessageKeys.TOTP_ACCOUNT_LICENSED);
                    return;
                }

                var message = messages.message(MessageKeys.TOTP_COMMON_USAGE);
                player.sendMessage(message);
            });
        } else {
            var message = messages.message(MessageKeys.ONLY_FOR_PLAYERS);
            source.sendMessage(message);
        }
    }

    @Execute(route = "enable")
    public void executeEnable(CommandSource source, String[] args) {
        if (source instanceof Player player) {
            var totpConfig = configuration.totpConfiguration();
            var accountOptional = accountManager.accountByName(player.getUsername());

            accountOptional.ifPresent(account -> {
                var totpEnabled = configuration.totpConfiguration().totpEnabled();
                if (!totpEnabled) {
                    var message = messages.message(MessageKeys.TOTP_FORCE_DISABLED);
                    player.sendMessage(message);
                    return;
                }

                if (account.licensed()) {
                    messages.sendMessage(player, MessageKeys.TOTP_ACCOUNT_LICENSED);
                    return;
                }

                if (account.hasTotp()) {
                    var message = messages.message(MessageKeys.TOTP_ALREADY_ENABLED);
                    player.sendMessage(message);
                    return;
                }

                if (args.length != 2) {
                    var message = messages.message(MessageKeys.TOTP_ENABLE_USAGE);
                    player.sendMessage(message);
                    return;
                }

                var password = account.hashedPassword();
                var hashedPassword = encryptor.encode(args[1]);

                if (!password.get().equals(hashedPassword)) {
                    var message = messages.message(MessageKeys.TOTP_WRONG_PASSWORD);
                    player.sendMessage(message);
                    return;
                }

                var totpToken = secretGenerator.generate();
                var issuser = totpConfig.totpIssuser();

                var qrData = new QrData.Builder()
                        .label(account.playerName())
                        .secret(totpToken)
                        .issuer(issuser)
                        .build();

                var qrUrl = totpConfig.totpQrGenerator().replace("%data%", URLEncoder.encode(qrData.getUri(), StandardCharsets.UTF_8));
                var codes = String.join(", ", this.codesGenerator.generateCodes(totpConfig.totpRecoveryCodesAmount()));

                account.updateTotpToken(totpToken);
                accountManager.updateAccount(account);
                storageManager.updateAccount(account);

                var successMessage = messages.message(MessageKeys.TOTP_SUCCESS_ENABLED,
                        "%url%", qrUrl,
                        "%token%", totpToken,
                        "%codes%", codes
                );

                player.sendMessage(successMessage);
            });
        } else {
            var message = messages.message(MessageKeys.ONLY_FOR_PLAYERS);
            source.sendMessage(message);
        }
    }

    @Execute(route = "disable")
    public void executeDisable(CommandSource source, String[] args, @Arg int code) {
        if (source instanceof Player player) {
            var accountOptional = accountManager.accountByName(player.getUsername());
            accountOptional.ifPresent(account -> {
                var totpEnabled = configuration.totpConfiguration().totpEnabled();
                if (!totpEnabled) {
                    var message = messages.message(MessageKeys.TOTP_FORCE_DISABLED);
                    player.sendMessage(message);
                    return;
                }

                if (account.licensed()) {
                    messages.sendMessage(player, MessageKeys.TOTP_ACCOUNT_LICENSED);
                    return;
                }

                if (args.length != 2) {
                    var message = messages.message(MessageKeys.TOTP_DISABLE_USAGE);
                    player.sendMessage(message);
                    return;
                }

                if (account.totpToken().isEmpty() && !account.hasTotp()) {
                    var message = messages.message(MessageKeys.TOTP_NOT_ENABLED);
                    player.sendMessage(message);
                    return;
                }

                var token = account.totpToken().get();
                var totpKey = String.valueOf(code);

                if (!totpManager.codeVerifier().isValidCode(token, totpKey)) {
                    var message = messages.message(MessageKeys.TOTP_WRONG);
                    player.sendMessage(message);
                    return;
                }

                account.updateTotpToken(null);

                accountManager.updateAccount(account);
                storageManager.updateAccount(account);

                var message = messages.message(MessageKeys.TOTP_SUCCESS_DISABLED);
                player.sendMessage(message);
            });
        } else {
            var message = messages.message(MessageKeys.ONLY_FOR_PLAYERS);
            source.sendMessage(message);
        }
    }
}
