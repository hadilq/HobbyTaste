package ir.asparsa.hobbytaste.server.security.model;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Holder for JWT token from the request.
 * <p/>
 * Other fields aren't used but necessary to comply to the contracts of AbstractUserDetailsAuthenticationProvider
 *
 * @author pascal alma
 */
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {


    private String token;
    private String address;
    private String uri;
    private String sessionId;

    public JwtAuthenticationToken(
            String token,
            String address,
            String uri,
            String sessionId
    ) {
        super(null, null);
        this.token = token;
        this.address = address;
        this.uri = uri;
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public String getAddress() {
        return address;
    }

    public String getUri() {
        return uri;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}