package pw.iwmc.authentic.configuration.nodes;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import pw.iwmc.authentic.api.configuration.enums.EncryptionMethod;
import pw.iwmc.authentic.api.configuration.nodes.AuthenticSecurityConfig;

import java.util.regex.Pattern;

@ConfigSerializable
public class SecurityConfiguration implements AuthenticSecurityConfig {

    @Setting("name-regex")
    protected String nameRegex = "^[A-Za-z0-9_]{3,16}$";

    @Setting("encryption-method")
    protected EncryptionMethod encryptionMethod = EncryptionMethod.SHA256;

    @Setting("min-password-length")
    protected int minPasswordLength = 6;

    @Setting("max-password-length")
    protected int maxPasswordLength = 32;

    @Setting("check-password-strength")
    protected boolean checkPasswordStrength = true;

    @Override
    public EncryptionMethod encryptionMethod() {
        return encryptionMethod;
    }

    @Override
    public Pattern nameRegex() {
        return Pattern.compile(nameRegex);
    }

    @Override
    public int minPasswordLength() {
        return minPasswordLength;
    }

    @Override
    public int maxPasswordLength() {
        return maxPasswordLength;
    }

    @Override
    public boolean checkPasswordStrength() {
        return checkPasswordStrength;
    }
}
