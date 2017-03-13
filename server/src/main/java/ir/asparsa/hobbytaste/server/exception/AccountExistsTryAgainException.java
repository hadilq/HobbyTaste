package ir.asparsa.hobbytaste.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Thrown when token cannot be found in the request header
 *
 * @author pascal alma
 */
@ResponseStatus(value = HttpStatus.NON_AUTHORITATIVE_INFORMATION, reason = "Exist")  // 203
public class AccountExistsTryAgainException extends BaseAuthenticationException {

    public AccountExistsTryAgainException(
            String message,
            String localizedMessageKey,
            String local
    ) {
        super(message, localizedMessageKey, local);
    }

    public AccountExistsTryAgainException(
            String message,
            Throwable cause,
            String localizedMessageKey,
            String local
    ) {
        super(message, cause, localizedMessageKey, local);
    }
}
