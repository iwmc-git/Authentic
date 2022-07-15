package pw.iwmc.authentic.configuration.nodes;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import pw.iwmc.authentic.api.configuration.nodes.AuthServersNode;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class PluginAuthServersNode implements AuthServersNode {

    @Setting("enable-virtual-server")
    protected boolean enableVirtualServer = false;

    @Setting("login-servers")
    protected List<String> loginServers = new ArrayList<>();

    @Setting("lobby-servers")
    protected List<String> lobbyServers = new ArrayList<>();

    @Override
    public boolean enableVirtualServer() {
        return enableVirtualServer;
    }

    @Override
    public List<String> loginServers() {
        return loginServers;
    }

    @Override
    public List<String> lobbyServers() {
        return lobbyServers;
    }
}
