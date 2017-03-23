package ir.asparsa.hobbytaste.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.asparsa.common.net.dto.ErrorDto;
import ir.asparsa.hobbytaste.server.controller.ServiceExceptionHandler;
import ir.asparsa.hobbytaste.server.exception.BaseAuthenticationException;
import ir.asparsa.hobbytaste.server.resources.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * @author hadi
 * @since 3/20/2017 AD.
 */
@Component
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFailureHandler.class);

    @Autowired
    private ReloadableResourceBundleMessageSource resourceBundle;

    @Override public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        logger.info("onAuthenticationFailure gets called");
        logger.warn("request: " + request, authException);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ErrorDto errorResponse;
        if (authException instanceof BaseAuthenticationException) {
            BaseAuthenticationException exception = (BaseAuthenticationException) authException;
            errorResponse = ServiceExceptionHandler
                    .generateError(authException, resourceBundle.getMessage(
                            exception.getLocalizedMessageKey(), null, new Locale(exception.getLocale())));
        } else {
            errorResponse = ServiceExceptionHandler.generateError(authException, resourceBundle.getMessage(
                    Strings.UNKNOWN, null, new Locale(Strings.DEFAULT_LOCALE)));
        }
        ObjectMapper mapper = new ObjectMapper();
        response.getOutputStream().println(mapper.writeValueAsString(errorResponse));
    }
}
