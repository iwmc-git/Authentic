package pw.iwmc.authentic.api.configuration.nodes;

public interface AuthenticTotpConfig {
    boolean totpEnabled();
    boolean totpNeedPassword();

    String totpIssuser();
    String totpQrGenerator();

    long totpRecoveryCodesAmount();
}
