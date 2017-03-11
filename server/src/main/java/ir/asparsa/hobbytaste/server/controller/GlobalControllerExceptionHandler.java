package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.common.net.dto.ErrorDto;
import ir.asparsa.common.net.dto.ResponseDto;
import ir.asparsa.hobbytaste.server.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

/**
 * @author hadi
 * @since 3/7/2017 AD.
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @Autowired
    private ReloadableResourceBundleMessageSource resourceBundle;

    @ExceptionHandler(InternalServerErrorException.class)
    public ErrorDto handleException(InternalServerErrorException e) {
        return getErrorDto(e);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    private ErrorDto handleException(CommentNotFoundException e) {
        return getErrorDto(e);
    }

    @ExceptionHandler(EmptyUsernameException.class)
    private ErrorDto handleException(EmptyUsernameException e) {
        return getErrorDto(e);
    }

    @ExceptionHandler(FilenameNotFoundErrorException.class)
    private ErrorDto handleException(FilenameNotFoundErrorException e) {
        return getErrorDto(e);
    }

    @ExceptionHandler(JwtTokenMalformedException.class)
    private ErrorDto handleException(JwtTokenMalformedException e) {
        return getErrorDto(e);
    }

    @ExceptionHandler(JwtTokenMissingException.class)
    private ErrorDto handleException(JwtTokenMissingException e) {
        return getErrorDto(e);
    }

    @ExceptionHandler(DdosSecurityException.class)
    private ErrorDto handleException(DdosSecurityException e) {
        return getErrorDto(e);
    }

    @ExceptionHandler(StorageException.class)
    private ErrorDto handleException(StorageException e) {
        return getErrorDto(e);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    private ErrorDto handleException(StorageFileNotFoundException e) {
        return getErrorDto(e);
    }

    @ExceptionHandler(StoreNotFoundException.class)
    private ErrorDto handleException(StoreNotFoundException e) {
        return getErrorDto(e);
    }

    private ErrorDto getErrorDto(Throwable e) {
        if (e instanceof BaseRuntimeException) {
            BaseRuntimeException exception = (BaseRuntimeException) e;
            return new ErrorDto(ResponseDto.STATUS.ERROR, e.getMessage(), resourceBundle.getMessage(
                    exception.getLocalizedMessageKey(), null, new Locale(exception.getLocale())));
        }
        return new ErrorDto(ResponseDto.STATUS.ERROR, e.getMessage(), e.getLocalizedMessage());
    }
}
