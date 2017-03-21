package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.common.net.dto.ErrorDto;
import ir.asparsa.common.net.dto.ResponseDto;
import ir.asparsa.hobbytaste.server.exception.BaseRuntimeException;
import ir.asparsa.hobbytaste.server.resources.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @author hadi
 * @since 3/19/2017 AD.
 */
@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(ServiceExceptionHandler.class);

    @Autowired
    private ReloadableResourceBundleMessageSource resourceBundle;

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    ResponseEntity<Object> handleControllerException(
            HttpServletRequest req,
            Throwable ex
    ) {
        logger.error("handleControllerException gets called", ex);

        ErrorDto errorResponse;
        HttpStatus httpStatus;
        if (ex instanceof BaseRuntimeException) {
            BaseRuntimeException exception = (BaseRuntimeException) ex;
            errorResponse = generateError(ex, resourceBundle.getMessage(
                    exception.getLocalizedMessageKey(), null, new Locale(exception.getLocale())));
            httpStatus = exception.getHttpStatus();
        } else {
            errorResponse = generateError(ex, resourceBundle.getMessage(
                    Strings.UNKNOWN, null, new Locale(Strings.DEFAULT_LOCALE)));
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    public static ErrorDto generateError(
            Throwable ex,
            String localizedMessage
    ) {
        return new ErrorDto(ResponseDto.STATUS.ERROR, ex.getMessage(), localizedMessage);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        logger.error("handleNoHandlerFoundException gets called", ex);
        return new ResponseEntity<>(generateError(ex, resourceBundle.getMessage(
                Strings.UNKNOWN, null, new Locale(Strings.DEFAULT_LOCALE))), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}