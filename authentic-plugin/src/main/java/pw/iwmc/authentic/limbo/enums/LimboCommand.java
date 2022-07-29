package pw.iwmc.authentic.limbo.enums;

public enum LimboCommand {
    REGISTER, LOGIN, TOTP, INVALID;

    public static LimboCommand parseCommand(String command) {
        return switch (command) {
            case "/login", "/l" -> LOGIN;
            case "/register", "/reg" -> REGISTER;
            case "/totp", "/2fa", "/2factor" -> TOTP;

            default -> INVALID;
        };
    }
}
