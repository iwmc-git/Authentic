package pw.iwmc.authentic.api.configuration.nodes;

public interface AuthenticMainConfig {
    long sessionTime();
    long authorizeTime();

    String licenseCheckUrl();
    String confirmKeyword();

    boolean licensedAutologin();
    boolean registerNeedRepeatPassword();
}
