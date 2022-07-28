package pw.iwmc.authentic.limbo;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;

import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboFactory;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.chunk.Dimension;
import net.elytrium.limboapi.api.chunk.VirtualWorld;
import net.elytrium.limboapi.api.player.GameMode;

import pw.iwmc.authentic.VelocityAuthentic;

public class PluginLimbo {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();

    private final LimboFactory limboFactory;
    private final Limbo limbo;

    public PluginLimbo() {
        var limboConfig = authentic.configuration().limboConfiguration();
        var proxyServer = authentic.proxyServer();
        var pluginManager = proxyServer.getPluginManager();

        this.limboFactory = (LimboFactory) pluginManager.getPlugin("limboapi").flatMap(PluginContainer::getInstance).orElseThrow();

        var world = makeWorld();

        authentic.defaultLogger().info("Creating limbo server...");
        this.limbo = limboFactory.createLimbo(world)
                .setName("Authentic")
                .setWorldTime(limboConfig.worldTics())
                .setGameMode(GameMode.CREATIVE);
    }

    public void passLimboPlayer(Player player) {
        limboFactory.passLoginLimbo(player);
    }

    public void spawnInLimbo(Player player, LimboSessionHandler handler) {
        authentic.defaultLogger().info("Spawning " + player.getUsername() + " into lobby..");
        limbo.spawnPlayer(player, handler);
    }

    private VirtualWorld makeWorld() {
        authentic.defaultLogger().info("Creating virtual world...");
        var limboConfig = authentic.configuration().limboConfiguration();

        return limboFactory.createVirtualWorld(
                Dimension.valueOf(limboConfig.dimension().name()),
                limboConfig.x(), limboConfig.y(), limboConfig.z(),
                limboConfig.yaw(), limboConfig.pitch()
        );
    }
}
