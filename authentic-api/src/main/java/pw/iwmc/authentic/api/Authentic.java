package pw.iwmc.authentic.api;

import pw.iwmc.authentic.api.configuration.AuthenticConfiguration;
import pw.iwmc.authentic.api.engine.AuthenticEngine;

public interface Authentic {
    AuthenticEngine engine();
    AuthenticConfiguration configuration();
}
