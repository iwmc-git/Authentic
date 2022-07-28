package pw.iwmc.authentic.api.configuration.nodes;

import java.util.List;

public interface AuthenticMainConfig {
    List<String> initialServers();

    long sessionTime();
    long authorizeTime();

    String licenseCheckUrl();
    String confirmKeyword();

    boolean licensedAutologin();
    boolean registerNeedRepeatPassword();
}
