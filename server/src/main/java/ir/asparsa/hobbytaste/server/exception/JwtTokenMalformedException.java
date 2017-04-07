package ir.asparsa.hobbytaste.server.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when token cannot be parsed
 *
 * @author pascal alma
 */
public class JwtTokenMalformedException extends BaseAuthenticationException {

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

    @Override public HttpStatus getHttpStatus() {
        return HttpStatus.NON_AUTHORITATIVE_INFORMATION;
    }
}
