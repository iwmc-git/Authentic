package pw.iwmc.authentic.commands.manager;

import com.velocitypowered.api.command.CommandSource;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.velocity.LiteVelocityFactory;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.commands.ChangePasswordCommand;
import pw.iwmc.authentic.commands.LicenseCommand;
import pw.iwmc.authentic.commands.LogoutCommand;
import pw.iwmc.authentic.commands.UnregisterCommand;

public class PluginCommandsManager {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();
    private final LiteCommands<CommandSource> liteCommands;

    public PluginCommandsManager() {
        this.liteCommands = LiteVelocityFactory.builder(authentic.proxyServer())
                .command(ChangePasswordCommand.class)
                .command(UnregisterCommand.class)
                .command(LogoutCommand.class)
                .command(LicenseCommand.class)
                .register();

    }

    public void unregisterAll() {
        liteCommands.getPlatform().unregisterAll();
    }
}
