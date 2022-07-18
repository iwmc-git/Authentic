package pw.iwmc.authentic.api.event;

public interface AuthenticationEvent {
    AuthenticationType type();

    enum AuthenticationType {
        LOGIN, REGISTER, UNREGISTER, SESSIONED, LICENSE_AUTOLOGIN
    }
}
