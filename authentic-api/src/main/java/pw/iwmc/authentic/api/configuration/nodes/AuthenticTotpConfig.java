package pw.iwmc.authentic.api.configuration.nodes;

public interface AuthenticTotpConfig {
    boolean totpEnabled();

    String totpIssuser();
    String totpQrGenerator();

    int totpRecoveryCodesAmount();
}
