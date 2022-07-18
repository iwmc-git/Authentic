package pw.iwmc.authentic.license;

import java.math.BigInteger;
import java.util.UUID;

public class LicensedPlayer {
    private final String id;
    private final String name;

    public LicensedPlayer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID licenseId() {
        return new UUID(
                new BigInteger(id.substring(0, 16), 16).longValue(),
                new BigInteger(id.substring(16, 32), 16).longValue()
        );
    }

    public String name() {
        return name;
    }
}
