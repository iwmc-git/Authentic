package pw.iwmc.authentic.events;

import pw.iwmc.authentic.api.event.AuthenticationEvent;

public class PluginAuthenticationEvent implements AuthenticationEvent {
    private AuthenticationType type;

    public PluginAuthenticationEvent(AuthenticationType type) {
        this.type = type;
    }

    @Override
    public AuthenticationType type() {
        return type;
    }

    public void updateType(AuthenticationType type) {
        this.type = type;
    }
}
