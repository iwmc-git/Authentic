package pw.iwmc.authentic.configuration.nodes;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import pw.iwmc.authentic.api.configuration.nodes.AuthenticMainConfig;

@ConfigSerializable
public class MainConfiguration implements AuthenticMainConfig {

    @Setting("authorize-time")
    protected long authorizeTime = 60000;

    @Setting("authorize-time")
    protected long sessionTime = 604800;

    @Setting("license-check-url")
    protected String licenseCheckUrl = "https://api.mojang.com/users/profiles/minecraft/%s";

    @Setting("licensed-autologin")
    protected boolean licensedAutologin = true;

    @Setting("ldebug")
    protected boolean debug = false;

    @Override
    public long sessionTime() {
        return sessionTime;
    }

    @Override
    public long authorizeTime() {
        return authorizeTime;
    }

    @Override
    public String licenseCheckUrl() {
        return licenseCheckUrl;
    }

    @Override
    public boolean debug() {
        return debug;
    }

    @Override
    public boolean licensedAutologin() {
        return licensedAutologin;
    }
}
