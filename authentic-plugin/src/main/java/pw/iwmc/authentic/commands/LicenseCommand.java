package pw.iwmc.authentic.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.managers.PluginAccountManager;

import java.util.List;

public class LicenseCommand implements SimpleCommand {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();
    private final PluginAccountManager accountManager = authentic.accountManager();

    @Override
    public void execute(Invocation invocation) {

    }

    @Override
    public List<String> suggest(Invocation invocation) {
        if (invocation.source() instanceof Player player && invocation.arguments().length == 0) {
            var accountOptional = accountManager.accountByName(player.getUsername());
            return accountOptional.map(account -> List.of(account.licensed() ? "discard" : "apply")).orElseThrow();
        } else {
            return List.of();
        }
    }
}
