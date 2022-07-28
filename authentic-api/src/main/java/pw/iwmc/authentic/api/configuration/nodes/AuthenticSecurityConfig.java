package pw.iwmc.authentic.api.configuration.nodes;

import pw.iwmc.authentic.api.configuration.enums.EncryptionMethod;

import java.util.regex.Pattern;

public interface AuthenticSecurityConfig {
    EncryptionMethod encryptionMethod();
    Pattern nameRegex();

    int minPasswordLength();
    int maxPasswordLength();

    boolean checkPasswordStrength();
}
