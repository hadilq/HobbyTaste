package ir.asparsa.hobbytaste.server.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.database.repository.AccountRepository;
import ir.asparsa.hobbytaste.server.exception.InternalServerErrorException;
import ir.asparsa.hobbytaste.server.resources.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Class validates a given token by using the secret configured in the application
 *
 * @author hadi
 * @since 12/1/2016 AD
 */
@Component
public class JwtTokenUtil {

    private final static Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.header}")
    private String header;

    @Autowired
    AccountRepository accountRepository;

    /**
     * Tries to parse specified String as a JWT token. If successful, returns User object with username, id and role prefilled (extracted from token).
     * If unsuccessful (token is invalid or not containing all required user properties), simply returns null.
     *
     * @param token the JWT token to parse
     * @return the User object extracted from specified token or null if a token is invalid.
     */
    public AccountModel parseToken(String token) {
        try {
            if (StringUtils.isEmpty(token)) {
                return null;
            }

            Claims body = Jwts.parser()
                              .setSigningKey(secret)
                              .parseClaimsJws(token)
                              .getBody();

            Optional<AccountModel> account =
                    accountRepository.findById(Long.parseLong((String) body.get("userId")));
            if (account.isPresent()) {
                String username = body.getSubject();
                logger.info("Username of token is " + username + " and username of database is " +
                            account.get().getUsername());
                if (!StringUtils.isEmpty(username) && !username.equals(account.get().getUsername())) {
                    return null;
                }

                return account.get();
            }
        } catch (JwtException e) {
            // Simply print the exception and null will be returned for the userDto
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Generates a JWT token containing username as subject, and userId and role as additional claims. These properties are taken from the specified
     * User object. Tokens validity is infinite.
     *
     * @param u the user for which the token will be generated
     * @return the JWT token
     */
    public String generateToken(AccountModel u) {
        Claims claims = Jwts.claims().setSubject(u.getUsername());
        claims.put("userId", u.getId() + "");
        claims.put("role", u.getRole());

        return Jwts.builder()
                   .setClaims(claims)
                   .signWith(SignatureAlgorithm.HS512, secret)
                   .compact();
    }

    public String getHeaderKey() {
        return header;
    }
}
