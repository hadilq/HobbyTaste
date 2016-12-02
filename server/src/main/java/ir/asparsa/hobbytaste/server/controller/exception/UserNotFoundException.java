package ir.asparsa.hobbytaste.server.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@ResponseStatus(HttpStatus.NOT_FOUND) public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("could not find user '" + userId + "'.");
    }
}