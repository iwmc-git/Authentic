package pw.iwmc.authentic.engine.storage;

import noelle.database.DefaultConnection;
import noelle.database.credentials.Credentials;
import noelle.database.h2.H2DatabaseConnection;
import noelle.database.mariadb.MariaDBDatabaseConnection;
import noelle.database.reader.SQLFileReader;

import org.jetbrains.annotations.Nullable;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.account.AuthenticPlayerAccount;
import pw.iwmc.authentic.api.account.AuthenticAccount;
import pw.iwmc.authentic.api.engine.storage.AuthenticStorage;
import pw.iwmc.authentic.api.engine.storage.StorageType;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.UUID;

public class PluginStorage implements AuthenticStorage {
    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();
    private final DefaultConnection connection;

    private SQLFileReader sqlFileReader;

    public PluginStorage() {
        var storageConfig = authentic.configuration().storage();
        var credentials = Credentials.credentialsOf(storageConfig.host(), storageConfig.database(), storageConfig.user(), storageConfig.password());

        this.connection = switch (storageConfig.storageType()) {
            case H2 -> {
                var includedStream = included("h2-schema.sql");
                this.sqlFileReader = SQLFileReader.readStream(includedStream);

                yield H2DatabaseConnection.newConnection(credentials, authentic.rootPath());
            }

            case MARIADB -> {
                var includedStream = included("mariadb-schema.sql");
                this.sqlFileReader = SQLFileReader.readStream(includedStream);

                yield MariaDBDatabaseConnection.newConnection(credentials);
            }
        };

        authentic.logger().info("Registered " + sqlFileReader.allQueries().size() + " database queries!");

        sqlFileReader.query("makeTable").ifPresent(sqlQuery -> connection.execute(sqlQuery.query()));

        var engine = authentic.engine();
        var query = sqlFileReader.query("mapIntoCache");
        query.ifPresent(sqlQuery -> connection.query(sqlQuery.query(), resultSet -> {
            try {
                while (resultSet.next()) {
                    var uniqueId = UUID.fromString(resultSet.getString(1));
                    var name = resultSet.getString(2);

                    var account = new AuthenticPlayerAccount(uniqueId, name);

                    var hashedPassword = resultSet.getString(3);
                    account.updateHashedPassword(hashedPassword);

                    var licenseId = resultSet.getString(4);
                    account.updateLicenseId(licenseId != null ? UUID.fromString(licenseId) : null);

                    var lastLoggedAddress = resultSet.getString(5);
                    account.updateLastLoggedAddress(lastLoggedAddress != null ? InetAddress.getByName(lastLoggedAddress) : null);

                    var sessionEndTime = resultSet.getTimestamp(6);
                    account.updateSessionEndDate(sessionEndTime);

                    engine.addCachedAccount(account);
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }

            return null;
        }));

        authentic.logger().info("Added " + engine.cachedAccounts().size() + " account into cache!");
    }

    private InputStream included(String name) {
        return PluginStorage.class.getClassLoader().getResourceAsStream("schema/" + name);
    }

    @Override
    public @Nullable AuthenticAccount fromStorage(String name) {
        var query = sqlFileReader.query("accountByName");

        query.ifPresent(sqlQuery -> connection.query(String.format(sqlQuery.query(), name), resultSet -> {
            try {
                if (resultSet.next()) {
                    var uniqueId = UUID.fromString(resultSet.getString(1));
                    var playerName = resultSet.getString(2);

                    var account = new AuthenticPlayerAccount(uniqueId, playerName);

                    var hashedPassword = resultSet.getString(3);
                    account.updateHashedPassword(hashedPassword);

                    var licenseId = resultSet.getString(4);
                    account.updateLicenseId(licenseId != null ? UUID.fromString(licenseId) : null);

                    var lastLoggedAddress = resultSet.getString(5);
                    account.updateLastLoggedAddress(lastLoggedAddress != null ? InetAddress.getByName(lastLoggedAddress) : null);

                    var sessionEndTime = resultSet.getTimestamp(6);
                    account.updateSessionEndDate(sessionEndTime);

                    return account;
                }

                return null;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }));

        return null;
    }

    @Override
    public @Nullable AuthenticAccount fromStorage(UUID playerUniqueId) {
        var query = sqlFileReader.query("accountByUniqueId");

        query.ifPresent(sqlQuery -> connection.query(String.format(sqlQuery.query(), playerUniqueId.toString()), resultSet -> {
            try {
                var uniqueId = UUID.fromString(resultSet.getString(1));
                var playerName = resultSet.getString(2);

                var account = new AuthenticPlayerAccount(uniqueId, playerName);

                var hashedPassword = resultSet.getString(3);
                account.updateHashedPassword(hashedPassword);

                var licenseId = resultSet.getString(4);
                account.updateLicenseId(licenseId != null ? UUID.fromString(licenseId) : null);

                var lastLoggedAddress = resultSet.getString(5);
                account.updateLastLoggedAddress(lastLoggedAddress != null ? InetAddress.getByName(lastLoggedAddress) : null);

                var sessionEndTime = resultSet.getTimestamp(6);
                account.updateSessionEndDate(sessionEndTime);

                return account;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }));

        return null;
    }

    @Override
    public StorageType storageType() {
        return authentic.configuration().storage().storageType();
    }

    @Override
    public void dropAccount(AuthenticAccount account) {
        var query = sqlFileReader.query("dropAccount");

        query.ifPresent(sqlQuery -> connection.execute(sqlQuery.query(), preparedStatement -> {
            try {
                preparedStatement.setString(1, account.playerUniqueId().toString());
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }));
    }

    @Override
    public void truncateAccount(AuthenticAccount account) {
        updateAccount(account);
    }

    @Override
    public void updateAccount(AuthenticAccount account) {
        var query = sqlFileReader.query("updateAccount");

        query.ifPresent(sqlQuery -> connection.execute(sqlQuery.query(), preparedStatement -> {
            try {
                preparedStatement.setString(1, account.playerUniqueId().toString());
                preparedStatement.setString(2, account.playerName());

                try {
                    var hashedPassword = account.hashedPassword();
                    if (hashedPassword != null && hashedPassword.isPresent()) {
                        preparedStatement.setString(3, hashedPassword.get());
                    } else {
                        preparedStatement.setString(3, null);
                    }

                    var playerLicenseId = account.playerLicenseId();
                    if (playerLicenseId != null && playerLicenseId.isPresent()) {
                        preparedStatement.setString(4, playerLicenseId.get().toString());
                    } else {
                        preparedStatement.setString(4, null);
                    }

                    var lastLoggedAddress = account.lastLoggedAddress();
                    if (lastLoggedAddress != null && lastLoggedAddress.isPresent()) {
                        preparedStatement.setString(5, lastLoggedAddress.get().getHostAddress());
                    } else {
                        preparedStatement.setString(5, null);
                    }

                    var sessionEndDate = account.sessionEndDate();
                    if (sessionEndDate != null && sessionEndDate.isPresent()) {
                        preparedStatement.setTimestamp(6, sessionEndDate.get());
                    } else {
                        preparedStatement.setString(6, null);
                    }
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }));
    }

    @Override
    public void makeAccount(AuthenticAccount account) {
        var query = sqlFileReader.query("makeAccount");

        query.ifPresent(sqlQuery -> connection.execute(sqlQuery.query(), preparedStatement -> {
            try {
                preparedStatement.setString(1, account.playerUniqueId().toString());
                preparedStatement.setString(2, account.playerName());

                try {
                    var hashedPassword = account.hashedPassword();
                    if (hashedPassword != null && hashedPassword.isPresent()) {
                        preparedStatement.setString(3, hashedPassword.get());
                    } else {
                        preparedStatement.setString(3, null);
                    }

                    var playerLicenseId = account.playerLicenseId();
                    if (playerLicenseId != null && playerLicenseId.isPresent()) {
                        preparedStatement.setString(4, playerLicenseId.get().toString());
                    } else {
                        preparedStatement.setString(4, null);
                    }

                    var lastLoggedAddress = account.lastLoggedAddress();
                    if (lastLoggedAddress != null && lastLoggedAddress.isPresent()) {
                        preparedStatement.setString(5, lastLoggedAddress.get().getHostAddress());
                    } else {
                        preparedStatement.setString(5, null);
                    }

                    var sessionEndDate = account.sessionEndDate();
                    if (sessionEndDate != null && sessionEndDate.isPresent()) {
                        preparedStatement.setTimestamp(6, sessionEndDate.get());
                    } else {
                        preparedStatement.setString(6, null);
                    }
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }));
    }

    public void close(boolean silent) {
        try {
            if (silent) {
                connection.closeSilent();
            } else {
                connection.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
