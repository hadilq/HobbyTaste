package ir.asparsa.hobbytaste.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author hadi
 * @since 12/11/2016 AD
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Username is empty")  // 500
public class InternalServerErrorException extends BaseRuntimeException {

    public InternalServerErrorException(
            String message,
            String localizedMessageKey,
            String local
    ) {
        super(message, localizedMessageKey, local);
    }

    public InternalServerErrorException(
            String message,
            Throwable cause,
            String localizedMessageKey,
            String local
    ) {
        super(message, cause, localizedMessageKey, local);
    }
}