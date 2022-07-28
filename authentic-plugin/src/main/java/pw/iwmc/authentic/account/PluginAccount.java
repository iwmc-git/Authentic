package pw.iwmc.authentic.account;

import pw.iwmc.authentic.api.account.AuthenticAccount;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

public class PluginAccount implements AuthenticAccount {
    private final String playerName;
    private UUID playerUniqueId;

    private Timestamp lastConnectedDate;
    private InetAddress lastConnectedAddress;

    private UUID playerLicenseId;
    private String hashedPassword;
    private InetAddress lastLoggedAddress;
    private Timestamp sessionEndDate;

    public PluginAccount(String playerName, UUID playerUniqueId) {
        this.playerName = playerName;
        this.playerUniqueId = playerUniqueId;
    }

    @Override
    public UUID playerUniqueId() {
        return playerUniqueId;
    }

    @Override
    public String playerName() {
        return playerName;
    }

    @Override
    public InetAddress lastConnectedAddress() {
        return lastConnectedAddress;
    }

    @Override
    public Timestamp lastConnectedDate() {
        return lastConnectedDate;
    }

    @Override
    public Optional<UUID> playerLicenseId() {
        return Optional.ofNullable(playerLicenseId);
    }

    @Override
    public Optional<String> hashedPassword() {
        return Optional.ofNullable(hashedPassword);
    }

    @Override
    public Optional<InetAddress> lastLoggedAddress() {
        return Optional.ofNullable(lastLoggedAddress);
    }

    @Override
    public Optional<Timestamp> sessionEndDate() {
        return Optional.ofNullable(sessionEndDate);
    }

    @Override
    public boolean licensed() {
        return playerLicenseId != null;
    }

    @Override
    public boolean registered() {
        return hashedPassword != null;
    }

    @Override
    public boolean logged() {
        if (sessionEndDate == null) {
            return false;
        }

        if (lastLoggedAddress == null) {
            return false;
        }

        if (!lastLoggedAddress.getHostAddress().equalsIgnoreCase(lastConnectedAddress.getHostAddress())) {
            return false;
        }

        return lastConnectedDate.before(sessionEndDate);
    }

    @Override
    public void updateHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    @Override
    public void updateLicenseId(UUID playerLicenseId) {
        this.playerLicenseId = playerLicenseId;
    }

    @Override
    public void updateLastLoggedAddress(InetAddress lastLoggedAddress) {
        this.lastLoggedAddress = lastLoggedAddress;
    }

    @Override
    public void updateSessionEndDate(Timestamp sessionEndDate) {
        this.sessionEndDate = sessionEndDate;
    }

    @Override
    public void updateUniqueId(UUID playerUniqueId) {
        this.playerUniqueId = playerUniqueId;
    }

    @Override
    public void updateLastConnectedAddress(InetAddress lastConnectedAddress) {
        this.lastConnectedAddress = lastConnectedAddress;
    }

    @Override
    public void updateLastConnectedDate(Timestamp lastConnectedDate) {
        this.lastConnectedDate = lastConnectedDate;
    }
}