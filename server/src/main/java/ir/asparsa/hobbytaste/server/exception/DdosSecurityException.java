package ir.asparsa.hobbytaste.server.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when token cannot be parsed
 *
 * @author pascal alma
 */
public class DdosSecurityException extends BaseAuthenticationException {

    public DdosSecurityException(
            String message,
            String localizedMessageKey,
            String local
    ) {
        super(message, localizedMessageKey, local);
    }

    public DdosSecurityException(
            String message,
            Throwable cause,
            String localizedMessageKey,
            String local
    ) {
        super(message, cause, localizedMessageKey, local);
    }

    @Override public HttpStatus getHttpStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
