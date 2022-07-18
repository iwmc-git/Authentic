package pw.iwmc.authentic.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;

import org.jetbrains.annotations.NotNull;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.api.engine.login.LoginMode;
import pw.iwmc.authentic.engine.PluginEngine;

public class PreLoginListener {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();
    private final PluginEngine engine = authentic.engine();

    @Subscribe(order = PostOrder.FIRST)
    public void onPreLogin(@NotNull PreLoginEvent event) {
        authentic.debug("Executing `onPreLogin` for " + event.getUsername());

        var username = event.getUsername();
        var cachedAccount = engine.byName(username);

        cachedAccount.ifPresent(entry -> {
            var account = entry.getValue();

            authentic.debug(account.playerName() + "`s license state - " + (account.licensed() ? "LICENSED" : "UNLICENSED"));

            var license = account.licensed();
            var loginMode = engine.currentLoginMode();

            var result = switch (loginMode) {
                case MIXED, UNIQUE -> {
                    yield license
                            ? PreLoginEvent.PreLoginComponentResult.forceOnlineMode()
                            : PreLoginEvent.PreLoginComponentResult.forceOfflineMode();
                }
                case CRACKED -> {
                    yield PreLoginEvent.PreLoginComponentResult.forceOfflineMode();
                }

                case MOJANG -> {

                }
            };

            event.setResult(result);
        });
    }
}
