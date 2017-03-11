package ir.asparsa.hobbytaste.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when token cannot be parsed
 *
 * @author pascal alma
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Forbidden")  // 403
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
}
