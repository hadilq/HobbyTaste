package ir.asparsa.hobbytaste.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author hadi
 * @since 12/11/2016 AD
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Username is empty")  // 400
public class EmptyUsernameException extends BaseRuntimeException {

    public EmptyUsernameException(
            String message,
            String localizedMessageKey,
            String local
    ) {
        super(message, localizedMessageKey, local);
    }

    public EmptyUsernameException(
            String message,
            Throwable cause,
            String localizedMessageKey,
            String local
    ) {
        super(message, cause, localizedMessageKey, local);
    }
}