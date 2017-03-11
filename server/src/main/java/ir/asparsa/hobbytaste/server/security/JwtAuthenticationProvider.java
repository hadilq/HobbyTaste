package ir.asparsa.hobbytaste.server.security;


import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.database.model.RequestLogModel;
import ir.asparsa.hobbytaste.server.database.repository.RequestLogRepository;
import ir.asparsa.hobbytaste.server.exception.DdosSecurityException;
import ir.asparsa.hobbytaste.server.exception.JwtTokenMalformedException;
import ir.asparsa.hobbytaste.server.resources.Strings;
import ir.asparsa.hobbytaste.server.security.model.AuthenticatedUser;
import ir.asparsa.hobbytaste.server.security.model.JwtAuthenticationToken;
import ir.asparsa.hobbytaste.server.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.util.List;

/**
 * Used for checking the token from the request and supply the UserDetails if the token is valid
 *
 * @author pascal alma
 */
@Component
public class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Value("${security.request.minInterval}")
    private int minInterval;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    RequestLogRepository requestLogRepository;

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
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String token = jwtAuthenticationToken.getToken();

        AccountModel parsedUser = jwtTokenUtil.parseToken(token);

        if (parsedUser == null) {
            throw new JwtTokenMalformedException(
                    "JWT token is not valid", Strings.JWT_TOKEN_NOT_VALID, Strings.DEFAULT_LOCALE);
        }

        long currentTime = System.currentTimeMillis();
        List<RequestLogModel> previousRequests = requestLogRepository
                .findTop10ByAddressOrderByDatetimeAsc(jwtAuthenticationToken.getAddress());
        if (previousRequests.size() == 10) {
            long averageTime = 0L;
            for (RequestLogModel previousRequest : previousRequests) {
                averageTime += previousRequest.getDatetime();
            }
            averageTime /= 10;
            if (currentTime - averageTime < minInterval) {
                throw new DdosSecurityException(
                        "JWT token is not valid", Strings.SECURITY_DDOS, Strings.DEFAULT_LOCALE);
            }
        }

        Observable.create((Observable.OnSubscribe<Void>) subscriber ->
                requestLogRepository
                        .save(new RequestLogModel(
                                currentTime, jwtAuthenticationToken.getAddress(),
                                jwtAuthenticationToken.getUri(), jwtAuthenticationToken.getSessionId(), parsedUser))
        )
                  .subscribeOn(Schedulers.newThread())
                  .subscribe(new Observer<Void>() {
                      @Override public void onCompleted() {
                      }

                      @Override public void onError(Throwable e) {
                      }

                      @Override public void onNext(Void aVoid) {
                      }
                  });

        List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(parsedUser.getRole());

        return new AuthenticatedUser(parsedUser.getId(), parsedUser.getUsername(), parsedUser, token, authorityList);
    }

}
