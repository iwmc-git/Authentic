package pw.iwmc.authentic.messages;

import noelle.features.messages.common.key.MessageKey;

public class MessageKeys {
    public final static MessageKey PREFIX = MessageKey.of("messages.prefix");
    public final static MessageKey ONLY_FOR_PLAYERS = MessageKey.of("messages.only-for-players");

    public final static MessageKey INVALID_NAME = MessageKey.of("messages.invalid-name");
    public final static MessageKey TIME_OUT = MessageKey.of("messages.time-out");

    public final static MessageKey SLASH_FIRST = MessageKey.of("messages.slash-first");

    public final static MessageKey UNSAFE_PASSWORD = MessageKey.of("messages.unsafe-password");
    public final static MessageKey UNKNOWN_PASSWORD = MessageKey.of("messages.unknown-password");

    public final static MessageKey PASSWORD_TOO_LONG = MessageKey.of("messages.password-too-long");
    public final static MessageKey PASSWORD_TOO_SHORT = MessageKey.of("messages.password-too-short");

    public final static MessageKey CHANGEPASSWORD_USAGE = MessageKey.of("messages.changepassword-command.usage");
    public final static MessageKey CHANGEPASSWORD_ACCOUNT_LICENSED = MessageKey.of("messages.changepassword-command.account-licensed");
    public final static MessageKey CHANGEPASSWORD_SUCCESS = MessageKey.of("messages.changepassword-command.success-message");
    public final static MessageKey CHANGEPASSWORD_HOVER = MessageKey.of("messages.changepassword-command.success-hover");
    public final static MessageKey CHANGEPASSWORD_WRONG_OLD_PASSWORD = MessageKey.of("messages.changepassword-command.old-password-wrong");

    public final static MessageKey LOGOUT_ACCOUNT_LICENSED = MessageKey.of("messages.logout-command.account-licensed");
    public final static MessageKey LOGOUT_SUCCESS = MessageKey.of("messages.logout-command.success-message");

    public final static MessageKey LICENSE_COMMON_USAGE = MessageKey.of("messages.license-command.common-usage");
    public final static MessageKey LICENSE_APPLY_USAGE = MessageKey.of("messages.license-command.apply-usage");
    public final static MessageKey LICENSE_NOT_FOUND = MessageKey.of("messages.license-command.no-license-found");
    public final static MessageKey LICENSE_ALREADY = MessageKey.of("messages.license-command.account-licensed");
    public final static MessageKey LICENSE_APPLIED = MessageKey.of("messages.license-command.success-apply");

    public final static MessageKey LICENSE_DISCARD_USAGE = MessageKey.of("messages.license-command.discard-usage");
    public final static MessageKey LICENSE_NOT_APPLIED = MessageKey.of("messages.license-command.account-not-licensed");
    public final static MessageKey LICENSE_DISCARDED = MessageKey.of("messages.license-command.success-discard");

    public final static MessageKey UNREGISTER_USAGE = MessageKey.of("messages.unregister-command.usage");
    public final static MessageKey UNREGISTER_ACCOUNT_LICENSED = MessageKey.of("messages.unregister-command.account-licensed");
    public final static MessageKey UNREGISTER_SUCCESS = MessageKey.of("messages.unregister-command.success-message");
    public final static MessageKey UNREGISTER_WRONG_PASSWORD = MessageKey.of("messages.unregister-command.password-wrong");

    public final static MessageKey LOGIN_REQUIRED_MESSAGE = MessageKey.of("messages.required.login-required-message");
    public final static MessageKey LOGIN_REQUIRED_TITLE = MessageKey.of("messages.required.login-required-title");
    public final static MessageKey LOGIN_REQUIRED_SUBTITLE = MessageKey.of("messages.required.login-required-subtitle");
    public final static MessageKey LOGIN_BOSSBAR_REMAINING = MessageKey.of("messages.required.bossbar-login-remaining-message");

    public final static MessageKey REGISTER_REQUIRED_MESSAGE = MessageKey.of("messages.required.register-required-message");
    public final static MessageKey REGISTER_REQUIRED_TITLE = MessageKey.of("messages.required.register-required-title");
    public final static MessageKey REGISTER_REQUIRED_SUBTITLE = MessageKey.of("messages.required.register-required-subtitle");
    public final static MessageKey REGISTER_BOSSBAR_REMAINING = MessageKey.of("messages.required.bossbar-register-remaining-message");

    public final static MessageKey REGISTER_NEED_REPEAT_MESSAGE = MessageKey.of("messages.required.register-need-repeat");
    public final static MessageKey REGISTER_REPEAT_PASSWORD_NOT_MATCH_MESSAGE = MessageKey.of("messages.required.register-repeat-password-not-match");

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
