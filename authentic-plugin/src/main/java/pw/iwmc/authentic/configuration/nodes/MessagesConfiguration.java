package pw.iwmc.authentic.configuration.nodes;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import pw.iwmc.authentic.api.configuration.enums.BossbarColor;
import pw.iwmc.authentic.api.configuration.enums.BossbarOverlay;
import pw.iwmc.authentic.api.configuration.nodes.AuthenticMessagesConfig;

import pw.iwmc.authentic.api.configuration.nodes.value.AuthenticTitleValues;
import pw.iwmc.authentic.configuration.nodes.value.PluginTitleValues;

@ConfigSerializable
public class MessagesConfiguration implements AuthenticMessagesConfig {

    @Setting("bossbar-enabled")
    protected boolean bossbarEnabled = false;

    @Setting("bossbar-overlay")
    protected BossbarOverlay bossbarOverlay = BossbarOverlay.PROGRESS;

    @Setting("bossbar-color")
    protected BossbarColor bossbarColor = BossbarColor.BLUE;

    @Setting("cyclic-messages-enabled")
    protected boolean cyclicMessagesEnabled = false;

    @Setting("titles-enabled")
    protected boolean titlesEnabled = false;

    @Setting("hovers-enabled")
    protected boolean hoversEnabled = false;

    @Setting("after-register-delay")
    protected long afterRegisterDelay = 1700;

    @Setting("after-login-delay")
    protected long afterLoginDelay = 1700;

    @Setting("after-license-login-delay")
    protected long afterLicenseLoginDelay = 1700;

    @Setting("after-session-login-delay")
    protected long afterSessionLoginDelay = 1700;

    @NodeKey
    @Setting("required-login-title-settings")
    protected PluginTitleValues requiredLoginTitleSettings = new PluginTitleValues();

    @NodeKey
    @Setting("required-register-title-settings")
    protected PluginTitleValues requiredRegisterTitleSettings = new PluginTitleValues();

    @NodeKey
    @Setting("logged-title-settings")
    protected PluginTitleValues loggedTitleSettings = new PluginTitleValues();

    @NodeKey
    @Setting("registered-title-settings")
    protected PluginTitleValues registeredTitleSettings = new PluginTitleValues();

    @NodeKey
    @Setting("license-logged-title-settings")
    protected PluginTitleValues licenseLoggedTitleSettings = new PluginTitleValues();

    @NodeKey
    @Setting("logged-from-session-title-settings")
    protected PluginTitleValues sessionLoggedTitleSettings = new PluginTitleValues();

    @NodeKey
    @Setting("totp-title-settings")
    protected PluginTitleValues totpTitleSettings = new PluginTitleValues();

    @Override
    public boolean cyclicMessagesEnabled() {
        return cyclicMessagesEnabled;
    }

    @Override
    public boolean bossbarEnabled() {
        return bossbarEnabled;
    }

    @Override
    public BossbarOverlay bossbarOverlay() {
        return bossbarOverlay;
    }

    @Override
    public BossbarColor bossbarColor() {
        return bossbarColor;
    }

    @Override
    public boolean titlesEnabled() {
        return titlesEnabled;
    }

    @Override
    public boolean hoversEnabled() {
        return hoversEnabled;
    }

    @Override
    public long afterRegisterDelay() {
        return afterRegisterDelay;
    }

    @Override
    public long afterLoginDelay() {
        return afterLoginDelay;
    }

    @Override
    public long afterLicenseLoginDelay() {
        return afterLicenseLoginDelay;
    }

    @Override
    public long afterSessionLoginDelay() {
        return afterSessionLoginDelay;
    }

    @Override
    public PluginTitleValues requiredLoginTitleSettings() {
        return requiredLoginTitleSettings;
    }

    @Override
    public PluginTitleValues requiredRegisterTitleSettings() {
        return requiredRegisterTitleSettings;
    }

    @Override
    public PluginTitleValues loggedTitleSettings() {
        return loggedTitleSettings;
    }

    @Override
    public PluginTitleValues registeredTitleSettings() {
        return registeredTitleSettings;
    }

    @Override
    public PluginTitleValues licenseLoggedTitleSettings() {
        return licenseLoggedTitleSettings;
    }

    @Override
    public PluginTitleValues loggedFromSessionTitleSettings() {
        return sessionLoggedTitleSettings;
    }

    @Override
    public PluginTitleValues totpTitleSettings() {
        return totpTitleSettings;
    }
}
