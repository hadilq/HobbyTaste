package ir.asparsa.hobbytaste.server.security;


import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.exception.JwtTokenMalformedException;
import ir.asparsa.hobbytaste.server.resources.Strings;
import ir.asparsa.hobbytaste.server.security.model.AuthenticatedUser;
import ir.asparsa.hobbytaste.server.security.model.JwtAuthenticationToken;
import ir.asparsa.hobbytaste.server.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Used for checking the token from the request and supply the UserDetails if the token is valid
 *
 * @author pascal alma
 */
@Component
public class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

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
    )
            throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String token = jwtAuthenticationToken.getToken();

        AccountModel parsedUser = jwtTokenUtil.parseToken(token);

        if (parsedUser == null) {
            throw new JwtTokenMalformedException(
                    "JWT token is not valid", Strings.JWT_TOKEN_NOT_VALID, Strings.DEFAULT_LOCALE);
        }

        List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(parsedUser.getRole());

        return new AuthenticatedUser(parsedUser.getId(), parsedUser.getUsername(), token, authorityList);
    }

}
