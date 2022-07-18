package pw.iwmc.authentic.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;

import org.jetbrains.annotations.NotNull;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.engine.PluginEngine;

import java.sql.Timestamp;

public class LoginListener {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();
    private final PluginEngine engine = authentic.engine();

    @Subscribe(order = PostOrder.FIRST)
    public void onLogin(@NotNull LoginEvent event) {
        authentic.debug("Executing `onPreLogin` for " + event.getPlayer().getUsername());

        var uniqueId = event.getPlayer().getUniqueId();
        var cachedAccount = engine.fromCache(uniqueId);

        cachedAccount.ifPresent(account -> {
            var currentConnectionAddress = event.getPlayer().getRemoteAddress();
            account.updateLastConnectedAddress(currentConnectionAddress.getAddress());

            authentic.debug("Current connection address for " + account.playerName() + ": " + currentConnectionAddress.getAddress().getHostAddress());

            var currentConnectionTime = new Timestamp(System.currentTimeMillis());
            account.updateLastConnectedDate(currentConnectionTime);

            authentic.debug("Current connection date for " + account.playerName() + ": " + currentConnectionTime);
        });
    }
}
