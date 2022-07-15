package pw.iwmc.authentic.api.configuration.nodes;

public interface SecurityNode {
    String encryptionMethod();

    int minimalPasswordLenght();
    int maximalPasswordLenght();

    TOTP totp();

    interface TOTP {
        boolean enabled();
        boolean mandatory();
    }
}
