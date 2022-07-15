package pw.iwmc.authentic.api.configuration.nodes;

import java.util.List;

public interface AuthServersNode {
    boolean enableVirtualServer();

    List<String> loginServers();
    List<String> lobbyServers();
}
