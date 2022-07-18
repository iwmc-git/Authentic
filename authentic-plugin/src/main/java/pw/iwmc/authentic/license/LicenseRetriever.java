package pw.iwmc.authentic.license;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

import pw.iwmc.authentic.VelocityAuthentic;
import pw.iwmc.authentic.api.account.AuthenticAccount;
import pw.iwmc.authentic.api.engine.license.LicenseServerMode;
import pw.iwmc.authentic.configuration.PluginConfiguration;

import java.io.InputStreamReader;
import java.net.URL;

public class LicenseRetriever {
    private final static Gson GSON = new GsonBuilder().create();

    private final VelocityAuthentic authentic = VelocityAuthentic.authentic();
    private final PluginConfiguration configuration = authentic.configuration();

    public LicensedPlayer retrieveFor(@NotNull AuthenticAccount account) {
        return retireveFor(account.playerName());
    }

    public LicensedPlayer retrieveFor(@NotNull Player player) {
        return retireveFor(player.getUsername());
    }

    public LicensedPlayer retireveFor(String playerName) {
        var url = LicenseServerMode.MINETOOLS.url() + playerName;

        try (var stream = new URL(url).openStream()) {
            var jsonObject = GSON.fromJson(new InputStreamReader(stream), JsonObject.class);

            var id = jsonObject.get("id").getAsString();
            var name = jsonObject.get("name").getAsString();

            if (id == null || id.isBlank() || id.isEmpty()) {
                return null;
            }

            return new LicensedPlayer(id, name);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
