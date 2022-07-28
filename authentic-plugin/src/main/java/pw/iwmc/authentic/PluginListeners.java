package pw.iwmc.authentic;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;

import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import net.kyori.adventure.title.Title;

import pw.iwmc.authentic.account.PluginAccount;
import pw.iwmc.authentic.configuration.PluginConfiguration;
import pw.iwmc.authentic.managers.PluginAccountManager;
import pw.iwmc.authentic.managers.PluginLicenseManager;
import pw.iwmc.authentic.managers.PluginStorageManager;
import pw.iwmc.authentic.messages.MessageKeys;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class PluginListeners {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();

    private final PluginConfiguration configuration = authentic.configuration();
    private final PluginAccountManager accountManager = authentic.accountManager();
    private final PluginLicenseManager licenseManager = authentic.licenseManager();
    private final PluginStorageManager storageManager = authentic.storageManager();

    @Subscribe(order = PostOrder.LAST)
    public void onPostLogin(PostLoginEvent event) {
        var postLoginTasks = accountManager.postLoginTasks();
        var postRegisterTasks = accountManager.postRegisterTasks();

        var messagesConfig = configuration.messagesConfiguration();
        var messages = authentic.messages();

        var player = event.getPlayer();
        var account = accountManager.accountById(player.getUniqueId());

        if (account.isEmpty()) {
            return;
        }

        var scheduler = authentic.proxyServer().getScheduler();

        var registerRunnable = postRegisterTasks.get(player.getUsername());
        if (registerRunnable != null) {
            scheduler.buildTask(authentic, () -> {
                registerRunnable.run();
                postRegisterTasks.remove(player.getUsername());
            }).delay(1, TimeUnit.SECONDS).schedule();
            return;
        }

        var loginRunnable = postLoginTasks.get(player.getUsername());
        if (loginRunnable != null) {
            scheduler.buildTask(authentic, () -> {
                loginRunnable.run();
                postLoginTasks.remove(player.getUsername());
            }).delay(1, TimeUnit.SECONDS).schedule();
            return;
        }

        scheduler.buildTask(authentic, () -> {
            if (!account.get().licensed()) {
                var message = messages.message(MessageKeys.LOGIN_FROM_SESSION_MESSAGE);
                player.sendMessage(message);

                if (messagesConfig.titlesEnabled()) {
                    var registerSuccess = messagesConfig.registeredTitleSettings();

                    var titleMessage = messages.message(MessageKeys.LOGIN_FROM_SESSION_TITLE);
                    var subtitleMessage = messages.message(MessageKeys.LOGIN_FROM_SESSION_SUBTITLE);

                    var fadeIn = Duration.ofMillis(registerSuccess.fadeIn());
                    var stay = Duration.ofMillis(registerSuccess.stay());
                    var fadeOut = Duration.ofMillis(registerSuccess.fadeOut());

                    var times = Title.Times.times(fadeIn, stay, fadeOut);
                    var title = Title.title(titleMessage, subtitleMessage, times);

                    player.showTitle(title);
                }
            } else {
                var message = messages.message(MessageKeys.LOGIN_FROM_LICENSE_MESSAGE);
                player.sendMessage(message);

                if (messagesConfig.titlesEnabled()) {
                    var registerSuccess = messagesConfig.registeredTitleSettings();

                    var titleMessage = messages.message(MessageKeys.LOGIN_FROM_LICENSE_TITLE);
                    var subtitleMessage = messages.message(MessageKeys.LOGIN_FROM_LICENSE_SUBTITLE);

                    var fadeIn = Duration.ofMillis(registerSuccess.fadeIn());
                    var stay = Duration.ofMillis(registerSuccess.stay());
                    var fadeOut = Duration.ofMillis(registerSuccess.fadeOut());

                    var times = Title.Times.times(fadeIn, stay, fadeOut);
                    var title = Title.title(titleMessage, subtitleMessage, times);

                    player.showTitle(title);
                }
            }
        }).delay(1, TimeUnit.SECONDS).schedule();
    }

    // WORK!!!!!
    @Subscribe()
    public void onPreLimboLogin(LoginLimboRegisterEvent event) {
        var cached = accountManager.accountByName(event.getPlayer().getUsername());
        if (cached.isEmpty()) {
            var username = event.getPlayer().getUsername();
            var uniqueId = event.getPlayer().getUniqueId();

            var connectionAddress = event.getPlayer().getRemoteAddress().getAddress();
            var connectionDate = new Timestamp(System.currentTimeMillis());

            var account = new PluginAccount(username, uniqueId);

            account.updateLastConnectedDate(connectionDate);
            account.updateLastConnectedAddress(connectionAddress);

            accountManager.addAccount(account);
            storageManager.insertAccount(account);
        }
    }

    // ?
    @Subscribe(order = PostOrder.LAST)
    public void onLimboLogin(LoginLimboRegisterEvent event) {
        var cached = accountManager.accountByName(event.getPlayer().getUsername());

        if (cached.isPresent()) {
            var account = cached.get();

            var connectionAddress = event.getPlayer().getRemoteAddress().getAddress();
            var connectionDate = new Timestamp(System.currentTimeMillis());

            account.updateLastConnectedDate(connectionDate);
            account.updateLastConnectedAddress(connectionAddress);

            if (!account.registered() || !account.logged()) {
                event.addOnJoinCallback(() -> accountManager.authorize(event.getPlayer(), account));
            }
        }
    }
}
