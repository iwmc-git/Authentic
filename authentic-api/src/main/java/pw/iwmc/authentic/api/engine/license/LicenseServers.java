package pw.iwmc.authentic.api.engine.license;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

public enum LicenseServers {
    MOJANG("https://api.mojang.com/users/profiles/minecraft/"),
    MINETOOLS("https://api.minetools.eu/uuid/");

    private final String url;

    LicenseServers(String url) {
        this.url = url;
    }

    public @NotNull String url() {
        return url;
    }

    @Contract(" -> new")
    public @NotNull URL toURL() {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
