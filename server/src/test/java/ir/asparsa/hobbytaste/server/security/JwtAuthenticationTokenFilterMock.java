package ir.asparsa.hobbytaste.server.security;

import ir.asparsa.hobbytaste.server.exception.JwtTokenMissingException;
import ir.asparsa.hobbytaste.server.resources.Strings;
import ir.asparsa.hobbytaste.server.security.model.JwtAuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter that orchestrates authentication by using supplied JWT token
 *
 * @author hadi
 * @since 12/1/2016 AD
 */
public class JwtAuthenticationTokenFilterMock extends AbstractAuthenticationProcessingFilter {

    private final static Logger logger = LoggerFactory.getLogger(JwtAuthenticationTokenFilterMock.class);

    private String token;

    public JwtAuthenticationTokenFilterMock(RequestMatcher matcher) {
        super(matcher);
    }

    /**
     * Attempt to authenticate request - basically just pass over to another method to authenticate request headers
     */
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        logger.debug("attemptAuthentication gets called");

        if (StringUtils.isEmpty(token)) {
            throw new JwtTokenMissingException(
                    "No JWT token found in request headers", Strings.NO_JWT_HEADER_FOUND, Strings.DEFAULT_LOCALE);
        }

        return getAuthenticationManager()
                .authenticate(new JwtAuthenticationToken(token, "", "", Strings.DEFAULT_LOCALE));
    }

    /**
     * Make sure the rest of the filterchain is satisfied
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    )
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);

        logger.debug("successfulAuthentication gets called");
        // As this authentication is in HTTP header, after success we need to continue the request normally
        // and return the response as if the resource was not secured at all
        chain.doFilter(request, response);
    }

    public void setToken(String token) {
        this.token = token;
    }
}