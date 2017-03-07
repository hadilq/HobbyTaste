package ir.asparsa.hobbytaste.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when token cannot be parsed
 *
 * @author pascal alma
 */
@ResponseStatus(value = HttpStatus.NON_AUTHORITATIVE_INFORMATION, reason = "Malformed")  // 203
public class JwtTokenMalformedException extends BaseAuthenticationException{

    public JwtTokenMalformedException(
            String message,
            String localizedMessageKey,
            String local
    ) {
        super(message, localizedMessageKey, local);
    }

    public JwtTokenMalformedException(
            String message,
            Throwable cause,
            String localizedMessageKey,
            String local
    ) {
        super(message, cause, localizedMessageKey, local);
    }
}
