package pw.iwmc.authentic.configuration.nodes;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import pw.iwmc.authentic.api.configuration.nodes.CachingNode;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigSerializable
public class PluginCachingNode implements CachingNode {

    @Setting("time")
    protected long time = 0;

    @Setting("unit")
    protected String unit = "";

    @Override
    public Duration cachingTime() {
        return Duration.of(time, ChronoUnit.valueOf(unit));
    }
}
