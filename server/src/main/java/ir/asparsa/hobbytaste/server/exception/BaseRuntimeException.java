package ir.asparsa.hobbytaste.server.exception;

/**
 * @author hadi
 * @since 3/7/2017 AD.
 */
public abstract class BaseRuntimeException extends RuntimeException {
    private String localizedMessageKey;
    private String locale;

    public BaseRuntimeException(
            String message,
            String localizedMessageKey,
            String local
    ) {
        super(message);
        this.localizedMessageKey = localizedMessageKey;
        this.locale = local;
    }

    public BaseRuntimeException(
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
