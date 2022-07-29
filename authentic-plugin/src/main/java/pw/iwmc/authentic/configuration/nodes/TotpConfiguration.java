package pw.iwmc.authentic.configuration.nodes;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import pw.iwmc.authentic.api.configuration.nodes.AuthenticTotpConfig;

@ConfigSerializable
public class TotpConfiguration implements AuthenticTotpConfig {

    @Setting("totp-enabled")
    protected boolean totpEnabled = true;

    @Setting("totp-issuser")
    protected String totpIssuser = "Authentic by Icewynd";

    @Setting("totp-qr-generator")
    protected String totpQrGenerator = "https://api.qrserver.com/v1/create-qr-code/?data={data}&size=200x200&ecc=M&margin=30";

    @Setting("totp-recovery-codes-amount")
    protected int totpRecoveryCodesAmount = 16;

    @Override
    public boolean totpEnabled() {
        return totpEnabled;
    }

    @Override
    public String totpIssuser() {
        return totpIssuser;
    }

    @Override
    public String totpQrGenerator() {
        return totpQrGenerator;
    }

    @Override
    public int totpRecoveryCodesAmount() {
        return totpRecoveryCodesAmount;
    }
}
