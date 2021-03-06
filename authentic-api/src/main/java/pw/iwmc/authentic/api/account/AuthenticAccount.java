package pw.iwmc.authentic.api.account;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public interface AuthenticAccount {
    UUID playerUniqueId();
    String playerName();

    InetAddress lastConnectedAddress();
    Timestamp lastConnectedDate();

    Optional<String> totpToken();
    Optional<UUID> playerLicenseId();
    Optional<String> hashedPassword();
    Optional<InetAddress> lastLoggedAddress();
    Optional<Timestamp> sessionEndDate();

    boolean licensed();
    boolean registered();
    boolean logged();

    boolean passedLogin();
    void passLogin(boolean pass);

    boolean hasTotp();

    void updateHashedPassword(String hashedPassword);
    void updateLicenseId(UUID playerLicenseId);
    void updateLastLoggedAddress(InetAddress lastLoggedAddress);
    void updateSessionEndDate(Timestamp sessionEndDate);

    void updateTotpToken(String totpToken);

    void updateLastConnectedAddress(InetAddress lastConnectedAddress);
    void updateLastConnectedDate(Timestamp lastConnectedDate);
}
