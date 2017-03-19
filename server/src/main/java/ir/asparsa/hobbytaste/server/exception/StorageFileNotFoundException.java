package ir.asparsa.hobbytaste.server.exception;

import org.springframework.http.HttpStatus;

public class StorageFileNotFoundException extends StorageException {

    public StorageFileNotFoundException(
            String message,
            String localizedMessageKey,
            String local
    ) {
        super(message, localizedMessageKey, local);
    }

    public StorageFileNotFoundException(
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