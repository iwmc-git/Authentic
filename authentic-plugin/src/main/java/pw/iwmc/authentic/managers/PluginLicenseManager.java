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
        var cachedLicenses = authentic.accountManager().cachedLicenses();

        var mainConfig = authentic.configuration().mainConfiguration();
        var url = String.format(mainConfig.licenseCheckUrl(), playerName);

        var cachedLicense = cachedLicenses.get(playerName);
        if (cachedLicense != null) {
            return cachedLicense;
        }

        try (var stream = new URL(url).openStream()) {
            var jsonObject = gson.fromJson(new InputStreamReader(stream), JsonObject.class);
            var id = jsonObject.get("id").getAsString();

            if (id == null || id.isBlank() || id.isEmpty()) {
                return null;
            }

            var performedUuid = new UUID(
                    new BigInteger(id.substring(0, 16), 16).longValue(),
                    new BigInteger(id.substring(16, 32), 16).longValue()
            );

            cachedLicenses.putIfAbsent(playerName, performedUuid);

            return performedUuid;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
