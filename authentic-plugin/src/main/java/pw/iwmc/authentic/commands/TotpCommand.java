package pw.iwmc.authentic.commands;

import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;

public class TotpCommand implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        invocation.source().sendMessage(Component.text("qwdqwdqwdqwdqwdqwdqwdqwdqwdqwd"));

    }
}
