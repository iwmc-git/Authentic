package pw.iwmc.authentic.configuration.nodes;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.NodeKey;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import pw.iwmc.authentic.api.configuration.enums.BossbarColor;
import pw.iwmc.authentic.api.configuration.enums.BossbarOverlay;
import pw.iwmc.authentic.api.configuration.nodes.AuthenticMessagesConfig;

import pw.iwmc.authentic.configuration.nodes.value.PluginTitleValues;

import java.util.concurrent.TimeUnit;

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

    @Setting("messages-delay-unit")
    protected TimeUnit messageDelayTimeUnit = TimeUnit.MILLISECONDS;

    @Setting("license-messages-delay")
    protected long licenseMessagesDelay = 0;

    @Setting("floodgate-messages-delay")
    protected long floodgateMessagesDelay = 0;

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
    public TimeUnit messageDelayTimeUnit() {
        return messageDelayTimeUnit;
    }

    @Override
    public long licenseMessagesDelay() {
        return licenseMessagesDelay;
    }

    @Override
    public long floodgateMessagesDelay() {
        return floodgateMessagesDelay;
    }
}
