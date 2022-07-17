package pw.iwmc.authentic.engine.storage;

import noelle.database.DefaultConnection;
import noelle.database.credentials.Credentials;
import noelle.database.reader.SQLFileReader;
import noelle.database.h2.H2Connection;
import noelle.database.mariadb.MariaDBConnection;

import org.jetbrains.annotations.Nullable;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.account.AuthenticPlayerAccount;
import pw.iwmc.authentic.api.account.AuthenticAccount;
import pw.iwmc.authentic.api.engine.storage.AuthenticStorage;
import pw.iwmc.authentic.api.engine.storage.StorageType;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

                yield H2Connection.makeConnection(credentials, authentic.rootPath());
            }

            case MARIADB -> {
                var includedStream = included("mariadb-schema.sql");
                this.sqlFileReader = SQLFileReader.readStream(includedStream);

                yield MariaDBConnection.makeConnection(credentials);
            }
        };

        authentic.debug("Registered " + sqlFileReader.allQueries().size() + " database queries!");

        sqlFileReader.query("makeTable").ifPresent(sqlQuery -> connection.execute(sqlQuery.query()).execute());

        var engine = authentic.engine();
        var query = sqlFileReader.query("mapIntoCache");

        query.ifPresent(sqlQuery -> connection.execute(sqlQuery.query()).prepare(preparedStatement -> {
            try {
                var resultSet = preparedStatement.executeQuery();

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
        }).execute());

        authentic.debug("Added " + engine.cachedAccounts().size() + " account into cache!");
    }

    private InputStream included(String name) {
        return PluginStorage.class.getClassLoader().getResourceAsStream("schema/" + name);
    }

    @Override
    public @Nullable AuthenticAccount fromStorage(String name) {
        var query = sqlFileReader.query("accountByName").get().query();

        return connection.query(String.format(query, name)).first(resultSet -> {
            var uniqueId = UUID.fromString(resultSet.getString(1));
            var playerName = resultSet.getString(2);

            var account = new AuthenticPlayerAccount(uniqueId, playerName);

            var hashedPassword = resultSet.getString(3);
            account.updateHashedPassword(hashedPassword);

            var licenseId = resultSet.getString(4);
            account.updateLicenseId(licenseId != null ? UUID.fromString(licenseId) : null);

            var lastLoggedAddress = resultSet.getString(5);

            try {
                account.updateLastLoggedAddress(lastLoggedAddress != null ? InetAddress.getByName(lastLoggedAddress) : null);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            var sessionEndTime = resultSet.getTimestamp(6);
            account.updateSessionEndDate(sessionEndTime);

            return account;
        }).orElse(null);
    }

    @Override
    public @Nullable AuthenticAccount fromStorage(UUID playerUniqueId) {
        var query = sqlFileReader.query("accountByUniqueId").get().query();

        return connection.query(String.format(query, playerUniqueId.toString())).first(resultSet -> {
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
        }).orElse(null);
    }

    @Override
    public StorageType storageType() {
        return authentic.configuration().storage().storageType();
    }

    @Override
    public void dropAccount(AuthenticAccount account) {
        var query = sqlFileReader.query("dropAccount");

        query.ifPresent(sqlQuery ->
                connection.execute(sqlQuery.query()).prepare(preparedStatement -> {
                    preparedStatement.setString(1, account.playerUniqueId().toString());

                    preparedStatement.closeOnCompletion();
                }).execute());
    }

    @Override
    public void truncateAccount(AuthenticAccount account) {
        updateAccount(account);
    }

    @Override
    public void updateAccount(AuthenticAccount account) {
        var query = sqlFileReader.query("updateAccount");

        query.ifPresent(sqlQuery -> connection.execute(sqlQuery.query()).prepare(preparedStatement -> {
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
        }).execute());
    }

    @Override
    public void makeAccount(AuthenticAccount account) {
        var query = sqlFileReader.query("makeAccount");

        query.ifPresent(sqlQuery -> connection.execute(sqlQuery.query()).prepare(preparedStatement -> {
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
        }).execute());
    }

    public void reconnect() {
        var query = sqlFileReader.query("reconnect");
        query.ifPresent(sqlQuery -> connection.execute(sqlQuery.query()).execute());
    }

    public void close() {
        connection.close();
    }
}
