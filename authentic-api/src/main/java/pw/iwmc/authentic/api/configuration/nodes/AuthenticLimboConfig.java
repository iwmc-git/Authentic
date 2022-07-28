package pw.iwmc.authentic.api.configuration.nodes;

import pw.iwmc.authentic.api.configuration.enums.LimboDimension;
import pw.iwmc.authentic.api.configuration.enums.WorldFileType;

public interface AuthenticLimboConfig {
    LimboDimension dimension();
    WorldFileType worldFileType();

    String worldFilePath();

    double x();
    double y();
    double z();

    float yaw();
    float pitch();

    boolean loadWorld();
    long worldTics();
}
