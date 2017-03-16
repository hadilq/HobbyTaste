package ir.asparsa.hobbytaste.core.retrofit;

import android.text.TextUtils;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.common.net.dto.AuthenticateDto;
import ir.asparsa.common.net.dto.OldTokenDto;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.BuildConfig;
import ir.asparsa.hobbytaste.core.manager.AuthorizationManager;
import ir.asparsa.hobbytaste.net.UserService;
import junit.framework.Assert;
import okhttp3.*;
import retrofit2.Retrofit;
import rx.Observer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.SecureRandom;

/**
 * @author hadi
 * @since 3/15/2017 AD.
 */
@Singleton
public class AuthorizationFactory implements Authenticator, Interceptor {


    private static final long MAX_REFRESH_STATE = 10000;

    private SecureRandom secureRandom = new SecureRandom();

    private long lastTimeRefreshed = System.currentTimeMillis();
    private long hashCode = generateHashCode();

    @Inject
    AuthorizationManager mAuthorizationManager;

    @Inject AuthorizationFactory() {
    }

    private long generateHashCode() {
        return lastTimeRefreshed ^ secureRandom.nextLong();
    }

    @Override public Request authenticate(
            Route route,
            Response response
    ) throws IOException {
        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {

            String oldToken = null;
            if (!TextUtils.isEmpty(response.request().header(BuildConfig.Authorization))) {
                oldToken = mAuthorizationManager.getToken();
                mAuthorizationManager.setToken(""); // If we already failed with these credentials.
            }

            return buildRequest(response.request(), oldToken);
        }
        return null;
    }

    @Override public Response intercept(Chain chain) throws IOException {
        return chain.proceed(buildRequest(chain.request(), null));
    }

    private Request buildRequest(
            Request original,
            String oldToken
    ) {
        if (!mAuthorizationManager.isAuthenticated()) {
            authorize(oldToken);
        }

        return original.newBuilder()
                       .header(BuildConfig.Authorization, mAuthorizationManager.getToken())
                       .header("Accept", "application/json")
                       .method(original.method(), original.body())
                       .build();
    }

    private void authorize(String oldToken) {
        long current = System.currentTimeMillis();
        if (current - lastTimeRefreshed > MAX_REFRESH_STATE) {
            lastTimeRefreshed = current;
            hashCode = generateHashCode();
        }
        new Authorization(hashCode, mAuthorizationManager, oldToken);
    }

    public static class Authorization {

        @Inject
        OkHttpClient.Builder mHttpClientBuilder;
        @Inject
        Retrofit.Builder mRetrofitBuilder;

        private Authorization(
                long hashCode,
                AuthorizationManager authorizationManager,
                String oldToken
        ) {
            ApplicationLauncher.mainComponent().inject(this);
            authorize(hashCode, authorizationManager, oldToken);
        }

        private void authorize(
                long hashCode,
                final AuthorizationManager authorizationManager,
                String oldToken
        ) {
            Retrofit retrofit = mRetrofitBuilder.client(mHttpClientBuilder.build()).build();
            retrofit.create(UserService.class)
                    .authenticate(hashCode, new OldTokenDto(oldToken))
                    .retry(5)
                    .toBlocking()
                    .subscribe(new Observer<AuthenticateDto>() {
                        @Override public void onCompleted() {
                        }

                        @Override public void onError(Throwable e) {
                            L.i(Authorization.class, "Error on authentication!", e);
                        }

                        @Override public void onNext(AuthenticateDto authenticateDto) {
                            String token = authenticateDto.getToken();
                            L.i(Authorization.class, "Token: " + token);
                            Assert.assertFalse(TextUtils.isEmpty(token));
                            authorizationManager.setToken(token);
                            authorizationManager.setUsername(authenticateDto.getUsername());
                        }
                    });

        }

    }
}
