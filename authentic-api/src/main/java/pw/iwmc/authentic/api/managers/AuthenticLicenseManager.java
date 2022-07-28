package pw.iwmc.authentic.api.managers;

import java.util.UUID;

public interface AuthenticLicenseManager {
    UUID retrieveFor(String playerName);
}
