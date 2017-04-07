package ir.asparsa.hobbytaste.server.exception;

import org.springframework.http.HttpStatus;

/**
 * @author hadi
 * @since 3/21/2017 AD.
 */
public class HashCodeExpiredException extends BaseRuntimeException {
    public HashCodeExpiredException(
            String message,
            String localizedMessageKey,
            String local
    ) {
        super(message, localizedMessageKey, local);
    }

    @Override public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_ACCEPTABLE;
    }
}
