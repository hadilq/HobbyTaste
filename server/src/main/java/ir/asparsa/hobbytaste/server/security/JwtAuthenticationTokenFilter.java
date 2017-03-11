package ir.asparsa.hobbytaste.server.security;

import ir.asparsa.hobbytaste.server.exception.JwtTokenMissingException;
import ir.asparsa.hobbytaste.server.resources.Strings;
import ir.asparsa.hobbytaste.server.security.model.JwtAuthenticationToken;
import ir.asparsa.hobbytaste.server.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
public class JwtAuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    public JwtAuthenticationTokenFilter(RequestMatcher matcher) {
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
        String authToken = request.getHeader(jwtTokenUtil.getHeaderKey());

        if (authToken == null) {
            throw new JwtTokenMissingException(
                    "No JWT token found in request headers", Strings.NO_JWT_HEADER_FOUND, Strings.DEFAULT_LOCALE);
        }

        JwtAuthenticationToken authRequest = new JwtAuthenticationToken(
                authToken, request.getRemoteAddr(), request.getRequestURI(), request.getRequestedSessionId());

        return getAuthenticationManager().authenticate(authRequest);
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

        // As this authentication is in HTTP header, after success we need to continue the request normally
        // and return the response as if the resource was not secured at all
        chain.doFilter(request, response);
    }
}