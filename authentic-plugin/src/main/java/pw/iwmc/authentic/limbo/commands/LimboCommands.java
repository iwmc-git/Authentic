package pw.iwmc.authentic.limbo.commands;

public class LimboCommands {
    private final LoginCommand loginCommand;
    private final RegisterCommand registerCommand;
    private final TotpCommand totpCommand;

    public LimboCommands() {
        this.loginCommand = new LoginCommand();
        this.registerCommand = new RegisterCommand();
        this.totpCommand = new TotpCommand();
    }

    public LoginCommand loginCommand() {
        return loginCommand;
    }

    public RegisterCommand registerCommand() {
        return registerCommand;
    }

    public TotpCommand totpCommand() {
        return totpCommand;
    }
}
