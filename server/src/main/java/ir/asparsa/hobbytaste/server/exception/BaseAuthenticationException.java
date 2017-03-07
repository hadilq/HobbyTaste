package ir.asparsa.hobbytaste.server.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author hadi
 * @since 3/7/2017 AD.
 */
public abstract class BaseAuthenticationException extends AuthenticationException {
    private String localizedMessageKey;
    private String locale;

    public BaseAuthenticationException(
            String message,
            String localizedMessageKey,
            String local
    ) {
        super(message);
        this.localizedMessageKey = localizedMessageKey;
        this.locale = local;
    }

    public BaseAuthenticationException(
            String message,
            Throwable cause,
            String localizedMessageKey,
            String local
    ) {
        super(message, cause);
        this.localizedMessageKey = localizedMessageKey;
        this.locale = local;
    }

    public String getLocalizedMessageKey() {
        return localizedMessageKey;
    }

    public String getLocale() {
        return locale;
    }
}
