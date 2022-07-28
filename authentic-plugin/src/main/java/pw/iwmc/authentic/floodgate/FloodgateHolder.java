package pw.iwmc.authentic.floodgate;

import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

public class FloodgateHolder {
    private final FloodgateApi floodgateApi;

    public FloodgateHolder() {
        this.floodgateApi = FloodgateApi.getInstance();
    }

    public boolean floodgatePlayer(UUID uuid) {
        return floodgateApi.isFloodgatePlayer(uuid);
    }

    public int prefixLength() {
        return floodgateApi.getPlayerPrefix().length();
    }
}
