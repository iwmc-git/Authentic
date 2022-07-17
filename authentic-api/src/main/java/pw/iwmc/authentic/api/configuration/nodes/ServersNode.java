package pw.iwmc.authentic.api.configuration.nodes;

import java.util.List;

public interface ServersNode {
    List<String> loginServers();
    List<String> lobbyServers();
}
