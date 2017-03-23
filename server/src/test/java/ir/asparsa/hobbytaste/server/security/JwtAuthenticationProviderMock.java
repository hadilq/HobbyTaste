package ir.asparsa.hobbytaste.server.security;


import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.security.model.AuthenticatedUser;
import ir.asparsa.hobbytaste.server.security.model.JwtAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Used for checking the token from the request and supply the UserDetails if the token is valid
 *
 * @author pascal alma
 */
@Component
public class JwtAuthenticationProviderMock extends AbstractUserDetailsAuthenticationProvider {

    private final static Logger logger = LoggerFactory.getLogger(JwtAuthenticationProviderMock.class);

    private AccountModel parsedUser;

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication
    )
            throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(
            String username,
            UsernamePasswordAuthenticationToken authentication
    ) throws AuthenticationException {
        logger.debug("retrieveUser gets called");

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        return new AuthenticatedUser(
                parsedUser.getId(), parsedUser.getUsername(), parsedUser, jwtAuthenticationToken.getToken(),
                AuthorityUtils.commaSeparatedStringToAuthorityList(parsedUser.getRole()));
    }

    public void setParsedUser(AccountModel parsedUser) {
        this.parsedUser = parsedUser;
    }
}
