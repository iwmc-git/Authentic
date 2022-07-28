package pw.iwmc.authentic.configuration.nodes;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import pw.iwmc.authentic.api.configuration.enums.LimboDimension;
import pw.iwmc.authentic.api.configuration.enums.WorldFileType;
import pw.iwmc.authentic.api.configuration.nodes.AuthenticLimboConfig;

@ConfigSerializable
public class LimboConfiguration implements AuthenticLimboConfig {

    @Setting("dimension")
    protected LimboDimension dimension = LimboDimension.THE_END;

    @Setting("load-world")
    protected boolean loadWorld = false;

    @Setting("world-file-type")
    protected WorldFileType worldFileType = WorldFileType.STRUCTURE;

    @Setting("world-file-path")
    protected String worldFilePath = "world.nbt";

    @Setting("world-ticks")
    protected long worldTics = 1000;

    @Setting("x-pos")
    protected double x = 0;

    @Setting("y-pos")
    protected double y = 0;

    @Setting("z-pos")
    protected double z = 0;

    @Setting("yaw-pos")
    protected float yaw = 0;

    @Setting("pitch-pos")
    protected float pitch = 0;

    @Override
    public LimboDimension dimension() {
        return dimension;
    }

    @Override
    public boolean loadWorld() {
        return loadWorld;
    }

    @Override
    public WorldFileType worldFileType() {
        return worldFileType;
    }

    @Override
    public String worldFilePath() {
        return worldFilePath;
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    @Override
    public float yaw() {
        return yaw;
    }

    @Override
    public float pitch() {
        return pitch;
    }

    @Override
    public long worldTics() {
        return worldTics;
    }
}
