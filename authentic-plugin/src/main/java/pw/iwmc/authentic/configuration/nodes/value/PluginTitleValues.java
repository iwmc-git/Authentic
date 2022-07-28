package pw.iwmc.authentic.configuration.nodes.value;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import pw.iwmc.authentic.api.configuration.nodes.value.AuthenticTitleValues;

@ConfigSerializable
public class PluginTitleValues implements AuthenticTitleValues {

    @Setting("fade-in")
    protected long fadeIn = 0;

    @Setting("fade-out")
    protected long fadeOut = 0;

    @Setting("stay")
    protected long stay = 0;

    @Override
    public long fadeIn() {
        return fadeIn;
    }

    @Override
    public long fadeOut() {
        return fadeOut;
    }

    @Override
    public long stay() {
        return stay;
    }
}
