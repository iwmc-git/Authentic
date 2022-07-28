package pw.iwmc.authentic.messages;

import noelle.features.messages.common.key.MessageKey;

public class MessageKeys {
    public final static MessageKey PREFIX = MessageKey.of("messages.prefix");

    public final static MessageKey INVALID_NAME = MessageKey.of("messages.invalid-name");
    public final static MessageKey TIME_OUT = MessageKey.of("messages.time-out");

    public final static MessageKey SLASH_FIRST = MessageKey.of("messages.slash-first");

    public final static MessageKey UNSAFE_PASSWORD = MessageKey.of("messages.unsafe-password");
    public final static MessageKey UNKNOWN_PASSWORD = MessageKey.of("messages.unknown-password");

    public final static MessageKey PASSWORD_TOO_LONG = MessageKey.of("messages.password-too-long");
    public final static MessageKey PASSWORD_TOO_SHORT = MessageKey.of("messages.password-too-short");

    public final static MessageKey LOGIN_REQUIRED_MESSAGE = MessageKey.of("messages.required.login-required-message");
    public final static MessageKey LOGIN_REQUIRED_TITLE = MessageKey.of("messages.required.login-required-title");
    public final static MessageKey LOGIN_REQUIRED_SUBTITLE = MessageKey.of("messages.required.login-required-subtitle");
    public final static MessageKey LOGIN_BOSSBAR_REMAINING = MessageKey.of("messages.required.bossbar-login-remaining-message");

    public final static MessageKey REGISTER_REQUIRED_MESSAGE = MessageKey.of("messages.required.register-required-message");
    public final static MessageKey REGISTER_REQUIRED_TITLE = MessageKey.of("messages.required.register-required-title");
    public final static MessageKey REGISTER_REQUIRED_SUBTITLE = MessageKey.of("messages.required.register-required-subtitle");
    public final static MessageKey REGISTER_BOSSBAR_REMAINING = MessageKey.of("messages.required.bossbar-register-remaining-message");

    public final static MessageKey LOGIN_FROM_SESSION_MESSAGE = MessageKey.of("messages.success.login-from-session-message");
    public final static MessageKey LOGIN_FROM_SESSION_TITLE = MessageKey.of("messages.success.login-from-session-title");
    public final static MessageKey LOGIN_FROM_SESSION_SUBTITLE = MessageKey.of("messages.success.login-from-session-subtitle");

    public final static MessageKey LOGIN_FROM_LICENSE_MESSAGE = MessageKey.of("messages.success.login-from-license-message");
    public final static MessageKey LOGIN_FROM_LICENSE_TITLE = MessageKey.of("messages.success.login-from-license-title");
    public final static MessageKey LOGIN_FROM_LICENSE_SUBTITLE = MessageKey.of("messages.success.login-from-license-subtitle");

    public final static MessageKey LOGIN_SUCCESS_MESSAGE = MessageKey.of("messages.success.login-success-message");
    public final static MessageKey LOGIN_SUCCESS_TITLE = MessageKey.of("messages.success.login-success-title");
    public final static MessageKey LOGIN_SUCCESS_SUBTITLE = MessageKey.of("messages.success.login-success-subtitle");

    public final static MessageKey REGISTER_SUCCESS_MESSAGE = MessageKey.of("messages.success.register-success-message");
    public final static MessageKey REGISTER_SUCCESS_TITLE = MessageKey.of("messages.success.register-success-title");
    public final static MessageKey REGISTER_SUCCESS_SUBTITLE = MessageKey.of("messages.success.register-success-subtitle");
}
