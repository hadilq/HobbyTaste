package ir.asparsa.hobbytaste.server.exception;

import org.springframework.http.HttpStatus;

/**
 * @author hadi
 * @since 12/11/2016 AD
 */
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

    @Override public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}