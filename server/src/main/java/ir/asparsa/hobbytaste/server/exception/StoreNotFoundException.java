package ir.asparsa.hobbytaste.server.exception;

import org.springframework.http.HttpStatus;

/**
 * @author hadi
 * @since 12/11/2016 AD
 */
public class StoreNotFoundException extends BaseRuntimeException {

    public StoreNotFoundException(
            String message,
            String localizedMessageKey,
            String local
    ) {
        super(message, localizedMessageKey, local);
    }

    public StoreNotFoundException(
            String message,
            Throwable cause,
            String localizedMessageKey,
            String local
    ) {
        super(message, cause, localizedMessageKey, local);
    }

    @Override public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}