package pw.iwmc.authentic.managers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.api.managers.AuthenticLicenseManager;

import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.util.UUID;

public class PluginLicenseManager implements AuthenticLicenseManager {
    private final VelocityAuthentic authentic  = VelocityAuthentic.authentic();
    private final Gson gson = new Gson();

    @Override
    public UUID retrieveFor(String playerName) {
        authentic.debug("Retrieving license id for " + playerName);

        var cachedLicenses = authentic.accountManager().cachedLicenses();

        var mainConfig = authentic.configuration().mainConfiguration();
        var url = String.format(mainConfig.licenseCheckUrl(), playerName);

        var cachedLicense = cachedLicenses.get(playerName);
        if (cachedLicense != null) {
            authentic.debug("License id for " + playerName + " found in cache!");
            return cachedLicense;
        }

        authentic.debug("License id for " + playerName + " not found in cache! Checking in external...");
        try (var stream = new URL(url).openStream()) {
            var jsonObject = gson.fromJson(new InputStreamReader(stream), JsonObject.class);

            var id = jsonObject.get("id").getAsString();
            var name = jsonObject.get("name").getAsString();

            if (id == null || id.isBlank() || id.isEmpty()) {
                authentic.debug("License id for " + playerName + " not found in external!");
                return null;
            }

            var performedUuid = new UUID(
                    new BigInteger(id.substring(0, 16), 16).longValue(),
                    new BigInteger(id.substring(16, 32), 16).longValue()
            );

            authentic.debug("Adding license id " + playerName + " into cache!");
            cachedLicenses.putIfAbsent(name, performedUuid);

            return performedUuid;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
