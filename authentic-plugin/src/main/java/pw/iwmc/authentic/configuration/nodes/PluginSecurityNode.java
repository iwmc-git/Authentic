package pw.iwmc.authentic.configuration.nodes;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import pw.iwmc.authentic.api.configuration.nodes.SecurityNode;

@ConfigSerializable
public class PluginSecurityNode implements SecurityNode {

    @Setting("encryption-method")
    protected String encryptionMethod = "SHA512";

    @Setting("minimal-password-lenght")
    protected int minimalPasswordLenght = 6;

    @Setting("maximal-password-lenght")
    protected int maximalPasswordLenght = 32;

    @NodeKey
    @Setting("totp")
    protected PluginTOTP totp = new PluginTOTP();

    @Override
    public String encryptionMethod() {
        return encryptionMethod;
    }

    @Override
    public int minimalPasswordLenght() {
        return minimalPasswordLenght;
    }

    @Override
    public int maximalPasswordLenght() {
        return maximalPasswordLenght;
    }

    @Override
    public TOTP totp() {
        return totp;
    }

    @ConfigSerializable
    public static class PluginTOTP implements TOTP {

        @Setting("enabled")
        protected boolean enabled = false;

        @Setting("mandatory")
        protected boolean mandatory = false;

        @Override
        public boolean enabled() {
            return enabled;
        }

        @Override
        public boolean mandatory() {
            return mandatory;
        }
    }
}
