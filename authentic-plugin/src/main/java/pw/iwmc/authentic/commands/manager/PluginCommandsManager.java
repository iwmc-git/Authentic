package pw.iwmc.authentic.commands.manager;

import com.velocitypowered.api.command.CommandSource;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.velocity.LiteVelocityFactory;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.commands.*;

public class PluginCommandsManager {
    private final LiteCommands<CommandSource> liteCommands;

    public PluginCommandsManager() {
        var authentic = VelocityAuthentic.authentic();
        authentic.defaultLogger().info("Registrering in-game commands...");

        this.liteCommands = LiteVelocityFactory.builder(authentic.proxyServer())
                .command(ChangePasswordCommand.class)
                .command(UnregisterCommand.class)
                .command(LogoutCommand.class)
                .command(LicenseCommand.class)
                .command(TotpCommand.class)
                .register();

    }

    public void unregisterAll() {
        liteCommands.getPlatform().unregisterAll();
    }
}
