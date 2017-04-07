package ir.asparsa.hobbytaste.server.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * Since our API only 'speaks' REST we give a HTTP 401 if user cannot be authenticated. There is no
 * login page top redirect to.
 *
 * @author pascal alma
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private final static Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    private static final long serialVersionUID = 3798723588865329956L;

    private AuthenticationFailureHandler failureHandler;

    @Autowired
    private ReloadableResourceBundleMessageSource resourceBundle;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        logger.debug("Commence gets called");
        // It shouldn't reach hear as long as exceptions catch by JwtAuthenticationFailureHandler
        try {
            failureHandler.onAuthenticationFailure(request, response, authException);
        } catch (ServletException e) {
            // Shouldn't happen
            logger.error("Calling failure is failed", e);
        }
    }

    public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }
}
