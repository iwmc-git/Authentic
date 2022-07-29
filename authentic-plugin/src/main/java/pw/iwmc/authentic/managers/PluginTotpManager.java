package pw.iwmc.authentic.managers;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;

public class PluginTotpManager {
    private final CodeVerifier codeVerifier;

    public PluginTotpManager() {
        var systemTimeProvider = new SystemTimeProvider();
        var defaultGenerator = new DefaultCodeGenerator();

        this.codeVerifier = new DefaultCodeVerifier(defaultGenerator, systemTimeProvider);
    }

    public CodeVerifier codeVerifier() {
        return codeVerifier;
    }
}
