package pw.iwmc.authentic.api.configuration.nodes;

import pw.iwmc.authentic.api.configuration.enums.BossbarColor;
import pw.iwmc.authentic.api.configuration.enums.BossbarOverlay;
import pw.iwmc.authentic.api.configuration.nodes.value.AuthenticTitleValues;

import java.util.concurrent.TimeUnit;

public interface AuthenticMessagesConfig {
    boolean bossbarEnabled();
    boolean cyclicMessagesEnabled();

    BossbarOverlay bossbarOverlay();
    BossbarColor bossbarColor();

    boolean titlesEnabled();
    boolean hoversEnabled();

    AuthenticTitleValues requiredLoginTitleSettings();
    AuthenticTitleValues requiredRegisterTitleSettings();

    AuthenticTitleValues loggedTitleSettings();
    AuthenticTitleValues registeredTitleSettings();

    AuthenticTitleValues licenseLoggedTitleSettings();

    TimeUnit messageDelayTimeUnit();

    long licenseMessagesDelay();
    long floodgateMessagesDelay();
}
