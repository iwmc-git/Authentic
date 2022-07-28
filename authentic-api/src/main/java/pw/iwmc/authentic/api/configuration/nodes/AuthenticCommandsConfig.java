package pw.iwmc.authentic.api.configuration.nodes;

import java.util.List;

public interface AuthenticCommandsConfig {
    String licenseCommand();
    String changepasswordCommand();
    String unregisterCommand();
    String totpCommand();

    List<String> licenseCommandAliases();
    List<String> changepasswordCommandAliases();
    List<String> unregisterCommandAliases();
    List<String> totpCommandAliases();

    List<String> licenseApplySubs();
    List<String> licenseDiscardSubs();
}
