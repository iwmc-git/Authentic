package pw.iwmc.authentic.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.engine.PluginEngine;

public class PostLoginListener {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();
    private final PluginEngine engine = authentic.engine();

    @Subscribe(order = PostOrder.FIRST)
    public void onPostLogin(PostLoginEvent event) {
        var cachedAccount = engine.fromCache(event.getPlayer().getUniqueId());

        cachedAccount.ifPresent(authenticAccount -> {
            if (!authenticAccount.logged());


        });
    }
}
