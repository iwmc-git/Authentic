package pw.iwmc.authentic.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;

import org.jetbrains.annotations.NotNull;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.account.AuthenticPlayerAccount;
import pw.iwmc.authentic.api.engine.login.LoginMode;
import pw.iwmc.authentic.configuration.PluginConfiguration;
import pw.iwmc.authentic.engine.PluginEngine;

import java.util.UUID;

public class GameProfileRequestListener {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();

    private final PluginEngine engine = authentic.engine();
    private final PluginConfiguration configuration = authentic.configuration();

    @Subscribe(order = PostOrder.FIRST, async = false)
    public void onGameProfileRequest(@NotNull GameProfileRequestEvent event) {
        authentic.logger().info("Executing `onGameProfileRequest` for " + event.getUsername());

        var id = event.getGameProfile().getId();
        var name = event.getGameProfile().getName();

        authentic.logger().info("Original UUID - " + id.toString());

        var accountOptional = engine.byName(name);
        if (accountOptional.isEmpty()) {
            authentic.logger().info("Account not found in cache! Creating new account...");

            var loginMode = configuration.loginMode();
            var prepareId = loginMode == LoginMode.UNIQUE || loginMode == LoginMode.MIXED ? UUID.randomUUID() : id;

            var account = new AuthenticPlayerAccount(prepareId, name);
            engine.makeAccount(account, false);
        }

        var makedOptionalAccount = engine.byName(name);
        makedOptionalAccount.ifPresent(entry -> {
            var gameProfile = event.getOriginalProfile();
            event.setGameProfile(gameProfile.withId(entry.getValue().playerUniqueId()));

            authentic.logger().info("Unique UUID - " + entry.getValue().playerUniqueId().toString());
        });
    }
}
