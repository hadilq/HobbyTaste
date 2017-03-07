package ir.asparsa.hobbytaste.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Thrown when token cannot be found in the request header
 *
 * @author pascal alma
 */
@ResponseStatus(value = HttpStatus.NON_AUTHORITATIVE_INFORMATION, reason = "Missing")  // 203
public class JwtTokenMissingException extends BaseAuthenticationException {

    public JwtTokenMissingException(
            String message,
            String localizedMessageKey,
            String local
    ) {
        super(message, localizedMessageKey, local);
    }

    public JwtTokenMissingException(
            String message,
            Throwable cause,
            String localizedMessageKey,
            String local
    ) {
        super(message, cause, localizedMessageKey, local);
    }
}
