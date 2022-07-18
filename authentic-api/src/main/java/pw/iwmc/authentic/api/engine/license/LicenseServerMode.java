package pw.iwmc.authentic.api.engine.license;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum LicenseServerMode {
    MOJANG("https://api.mojang.com/users/profiles/minecraft/"),
    MINETOOLS("https://api.minetools.eu/uuid/");

    private final String url;

    LicenseServerMode(String url) {
        this.url = url;
    }

    @Contract(" -> new")
    public @NotNull String url() {
        return url;
    }
}
