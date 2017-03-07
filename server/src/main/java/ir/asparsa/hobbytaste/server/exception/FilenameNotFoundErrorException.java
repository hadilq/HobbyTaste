package ir.asparsa.hobbytaste.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author hadi
 * @since 12/11/2016 AD
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Filename not found")  // 404
public class FilenameNotFoundErrorException extends BaseRuntimeException{

    public FilenameNotFoundErrorException(
            String message,
            String localizedMessageKey,
            String local
    ) {
        super(message, localizedMessageKey, local);
    }

    public FilenameNotFoundErrorException(
            String message,
            Throwable cause,
            String localizedMessageKey,
            String local
    ) {
        super(message, cause, localizedMessageKey, local);
    }
}