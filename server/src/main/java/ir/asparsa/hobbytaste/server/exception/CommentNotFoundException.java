package ir.asparsa.hobbytaste.server.exception;

import org.springframework.http.HttpStatus;

/**
 * @author hadi
 * @since 12/11/2016 AD
 */
public class CommentNotFoundException extends BaseRuntimeException {

    public CommentNotFoundException(
            String message,
            String localizedMessageKey,
            String local
    ) {
        super(message, localizedMessageKey, local);
    }

    public CommentNotFoundException(
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