package pw.iwmc.authentic.managers;

import noelle.database.DefaultConnection;
import noelle.database.credentials.Credentials;
import noelle.database.reader.SQLFileReader;
import noelle.database.types.h2.H2Connection;
import noelle.database.types.mariadb.MariaDBConnection;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.account.PluginAccount;
import pw.iwmc.authentic.api.account.AuthenticAccount;
import pw.iwmc.authentic.api.configuration.enums.StorageType;
import pw.iwmc.authentic.api.managers.AuthenticStorageManager;

import java.net.InetAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PluginStorageManager implements AuthenticStorageManager {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();

    private DefaultConnection connection;
    private SQLFileReader sqlFileReader;

    public PluginStorageManager() {
        authentic.defaultLogger().info("Loading storage manager...");

        var storageConfig = authentic.configuration().storageConfiguration();
        var credentials = makeCredentials();

        var verify = verify();
        if (!verify) {
            authentic.defaultLogger().error("Something went wrong!");
            return;
        }

        var storageType = storageConfig.storageType();
        authentic.defaultLogger().info("Loading storage provider - " + storageType.name());

        this.connection = switch (storageType) {
            case H2 -> H2Connection.makeConnection(credentials, authentic.rootPath());
            case MARIADB -> MariaDBConnection.makeConnection(credentials);
        };

        this.sqlFileReader = initReader();

        makeTable();
        mapInCache();
        reconnect();
    }

    public void close() {
        connection.close();
    }

    @Override
    public Optional<AuthenticAccount> accountByName(String name) {
        var query = sqlFileReader.query("accountByName");
        return query.flatMap(sqlQuery -> connection.query(String.format(sqlQuery.query(), name)).first(resultSet -> {
            try {
                var uniqueId = UUID.fromString(resultSet.getString(1));
                var playerName = resultSet.getString(2);

                var totpToken = resultSet.getString(3);

                var hashedPassword = resultSet.getString(4);
                var licenseId = resultSet.getString(5);

                var lastLoggedAddress = resultSet.getString(6);
                var endSessionDate = resultSet.getTimestamp(7);

                var account = new PluginAccount(playerName, uniqueId);
                account.updateHashedPassword(hashedPassword);
                account.updateSessionEndDate(endSessionDate);
                account.updateTotpToken(totpToken);

                if (licenseId != null) {
                    account.updateLicenseId(UUID.fromString(licenseId));
                }

                if (lastLoggedAddress != null) {
                    account.updateLastLoggedAddress(InetAddress.getByName(lastLoggedAddress));
                }

                return account;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }));
    }

    @Override
    public Optional<AuthenticAccount> accountById(UUID id) {
        var query = sqlFileReader.query("accountByUniqueId");
        return query.flatMap(sqlQuery -> connection.query(String.format(sqlQuery.query(), id)).first(resultSet -> {
            try {
                var uniqueId = UUID.fromString(resultSet.getString(1));
                var playerName = resultSet.getString(2);

                var totpToken = resultSet.getString(3);

                var hashedPassword = resultSet.getString(4);
                var licenseId = resultSet.getString(5);

                var lastLoggedAddress = resultSet.getString(6);
                var endSessionDate = resultSet.getTimestamp(7);

                var account = new PluginAccount(playerName, uniqueId);
                account.updateHashedPassword(hashedPassword);
                account.updateSessionEndDate(endSessionDate);
                account.updateTotpToken(totpToken);

                if (licenseId != null) {
                    account.updateLicenseId(UUID.fromString(licenseId));
                }

                if (lastLoggedAddress != null) {
                    account.updateLastLoggedAddress(InetAddress.getByName(lastLoggedAddress));
                }

                return account;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }));
    }

    @Override
    public void insertAccount(AuthenticAccount account) {
        var existsAccount = accountByName(account.playerName());
        if (existsAccount.isPresent()) {
            return;
        }

        authentic.defaultLogger().info("Creating account for " + account.playerName() + " in database...");
        var query = sqlFileReader.query("makeAccount");

        query.ifPresent(sqlQuery -> connection.execute(sqlQuery.query()).prepare(preparedStatement -> {
            preparedStatement.setString(1, account.playerUniqueId().toString());
            preparedStatement.setString(2, account.playerName());

            try {
                var totpToken = account.hashedPassword();
                if (totpToken != null && totpToken.isPresent()) {
                    preparedStatement.setString(3, totpToken.get());
                } else {
                    preparedStatement.setString(3, null);
                }

                var hashedPassword = account.hashedPassword();
                if (hashedPassword != null && hashedPassword.isPresent()) {
                    preparedStatement.setString(4, hashedPassword.get());
                } else {
                    preparedStatement.setString(4, null);
                }

                var playerLicenseId = account.playerLicenseId();
                if (playerLicenseId != null && playerLicenseId.isPresent()) {
                    preparedStatement.setString(5, playerLicenseId.get().toString());
                } else {
                    preparedStatement.setString(5, null);
                }

                var lastLoggedAddress = account.lastLoggedAddress();
                if (lastLoggedAddress != null && lastLoggedAddress.isPresent()) {
                    preparedStatement.setString(6, lastLoggedAddress.get().getHostAddress());
                } else {
                    preparedStatement.setString(6, null);
                }

                var sessionEndDate = account.sessionEndDate();
                if (sessionEndDate != null && sessionEndDate.isPresent()) {
                    preparedStatement.setTimestamp(7, sessionEndDate.get());
                } else {
                    preparedStatement.setString(7, null);
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }).execute());
    }

    @Override
    public void updateAccount(AuthenticAccount account) {
        var existsAccount = accountByName(account.playerName());
        if (existsAccount.isEmpty()) {
            insertAccount(account);
            return;
        }

        authentic.defaultLogger().info("Updating " + account.playerName() + " account in database...");
        var query = sqlFileReader.query("updateAccount");

        query.ifPresent(sqlQuery -> connection.execute(sqlQuery.query()).prepare(preparedStatement -> {
            preparedStatement.setString(1, account.playerUniqueId().toString());
            preparedStatement.setString(2, account.playerName());

            preparedStatement.setString(8, account.playerName());

            try {
                var totpToken = account.totpToken();
                if (totpToken != null && totpToken.isPresent()) {
                    preparedStatement.setString(3, totpToken.get());
                } else {
                    preparedStatement.setString(3, null);
                }

                var hashedPassword = account.hashedPassword();
                if (hashedPassword != null && hashedPassword.isPresent()) {
                    preparedStatement.setString(4, hashedPassword.get());
                } else {
                    preparedStatement.setString(4, null);
                }

                var playerLicenseId = account.playerLicenseId();
                if (playerLicenseId != null && playerLicenseId.isPresent()) {
                    preparedStatement.setString(5, playerLicenseId.get().toString());
                } else {
                    preparedStatement.setString(5, null);
                }

                var lastLoggedAddress = account.lastLoggedAddress();
                if (lastLoggedAddress != null && lastLoggedAddress.isPresent()) {
                    preparedStatement.setString(6, lastLoggedAddress.get().getHostAddress());
                } else {
                    preparedStatement.setString(6, null);
                }

                var sessionEndDate = account.sessionEndDate();
                if (sessionEndDate != null && sessionEndDate.isPresent()) {
                    preparedStatement.setTimestamp(7, sessionEndDate.get());
                } else {
                    preparedStatement.setString(7, null);
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }).execute());
    }

    @Override
    public void dropAccount(AuthenticAccount account) {
        var query = sqlFileReader.query("dropAccount");

        query.ifPresent(sqlQuery -> connection.execute(sqlQuery.query())
                .prepare(preparedStatement -> preparedStatement.setString(1, account.playerUniqueId().toString()))
                .execute()
        );
    }

    // =================== Private methods =================== //

    private void reconnect() {
        var scheduler = authentic.proxyServer().getScheduler();

        scheduler.buildTask(authentic, () -> connection.execute("SELECT 1").execute())
                .delay(20, TimeUnit.SECONDS)
                .repeat(20, TimeUnit.SECONDS)
                .schedule();
    }

    private void makeTable() {
        authentic.defaultLogger().info("Creating new table...");
        sqlFileReader.query("makeTable").ifPresent(sqlQuery -> connection.execute(sqlQuery.query()).execute());
    }

    private void mapInCache() {
        authentic.defaultLogger().info("Mapping database accounts in cache...");
        var query = sqlFileReader.query("mapIntoCache");

        query.ifPresent(sqlQuery -> connection.execute(sqlQuery.query()).prepare(preparedStatement -> {
            try {
                var resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    var uniqueId = UUID.fromString(resultSet.getString(1));
                    var name = resultSet.getString(2);

                    var account = new PluginAccount(name, uniqueId);

                    var totpToken = resultSet.getString(3);
                    account.updateTotpToken(totpToken);

                    var hashedPassword = resultSet.getString(4);
                    account.updateHashedPassword(hashedPassword);

                    var licenseId = resultSet.getString(5);
                    account.updateLicenseId(licenseId != null ? UUID.fromString(licenseId) : null);

                    var lastLoggedAddress = resultSet.getString(6);
                    account.updateLastLoggedAddress(lastLoggedAddress != null ? InetAddress.getByName(lastLoggedAddress) : null);

                    var sessionEndTime = resultSet.getTimestamp(7);
                    account.updateSessionEndDate(sessionEndTime);

                    authentic.accountManager().addAccount(account);
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }).execute());

        var cached = authentic.accountManager().cachedAccounts();
        authentic.defaultLogger().info("Mapped cached accounts - " + cached.size());
    }

    private SQLFileReader initReader() {
        authentic.defaultLogger().info("Loading sql reader...");

        var storageConfig = authentic.configuration().storageConfiguration();
        var resourcePath = "pw/iwmc/authentic/files/schema/";

        try {
            var resource = switch (storageConfig.storageType()) {
                case MARIADB -> getClass().getClassLoader().getResourceAsStream(resourcePath + "mariadb-schema.sql");
                case H2 -> getClass().getClassLoader().getResourceAsStream(resourcePath + "h2-schema.sql");
            };

            assert resource != null;
            return SQLFileReader.readStream(resource);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private boolean verify() {
        var storageConfig = authentic.configuration().storageConfiguration();

        if (storageConfig.storageType() == StorageType.H2) {
            return true;
        }

        authentic.defaultLogger().info("Verifying database credentials...");

        var username = storageConfig.username();
        var password = storageConfig.password();
        var database = storageConfig.database();

        if (username.equalsIgnoreCase("username")) {
            authentic.defaultLogger().error("Field `username` is default value!");
            return false;
        }

        if (password.equalsIgnoreCase("password")) {
            authentic.defaultLogger().error("Field `password` is default value!");
            return false;
        }

        if (database.equalsIgnoreCase("database")) {
            authentic.defaultLogger().error("Field `database` is default value!");
            return false;
        }

        authentic.defaultLogger().info("Credentials success verified!");
        return true;
    }

    private Credentials makeCredentials() {
        authentic.defaultLogger().info("Preparing credentials...");
        var storageConfig = authentic.configuration().storageConfiguration();

        var hostname = String.format("%s:%s", storageConfig.hostname(), storageConfig.port());
        var username = storageConfig.username();
        var password = storageConfig.password();
        var database = storageConfig.database();

        return Credentials.credentialsOf(hostname, database, username, password);
    }
}
