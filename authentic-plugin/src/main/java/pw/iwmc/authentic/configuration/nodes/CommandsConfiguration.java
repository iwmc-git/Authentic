package pw.iwmc.authentic.configuration.nodes;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import pw.iwmc.authentic.api.configuration.nodes.AuthenticCommandsConfig;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class CommandsConfiguration implements AuthenticCommandsConfig {

    @Setting("license-command")
    protected String licenseCommand = "license";

    @Setting("changepassword-command")
    protected String changepasswordCommand = "changepassword";

    @Setting("unregister-command")
    protected String unregisterCommand = "unregister";

    @Setting("totp-command")
    protected String totpCommand = "totp";

    @Setting("license-command-aliases")
    protected List<String> licenseCommandAliases = new ArrayList<>();

    @Setting("changepassword-command-aliases")
    protected List<String> changepassCommandAliases = new ArrayList<>();

    @Setting("nregister-command-aliases")
    protected List<String> unregisterCommandAliases = new ArrayList<>();

    @Setting("totp-command-aliases")
    protected List<String> totpCommandAliases = new ArrayList<>();

    @Setting("license-apply-subs")
    protected List<String> licenseApplySubs = new ArrayList<>();

    @Setting("license-discard-subs")
    protected List<String> licenseDiscardSubs = new ArrayList<>();

    @Override
    public String licenseCommand() {
        return licenseCommand;
    }

    @Override
    public String changepasswordCommand() {
        return changepasswordCommand;
    }

    @Override
    public String unregisterCommand() {
        return unregisterCommand;
    }

    @Override
    public String totpCommand() {
        return totpCommand;
    }

    @Override
    public List<String> licenseCommandAliases() {
        return licenseCommandAliases;
    }

    @Override
    public List<String> changepasswordCommandAliases() {
        return changepassCommandAliases;
    }

    @Override
    public List<String> unregisterCommandAliases() {
        return unregisterCommandAliases;
    }

    @Override
    public List<String> totpCommandAliases() {
        return totpCommandAliases;
    }

    @Override
    public List<String> licenseApplySubs() {
        return licenseApplySubs;
    }

    @Override
    public List<String> licenseDiscardSubs() {
        return licenseDiscardSubs;
    }
}
