package ir.asparsa.hobbytaste.core.retrofit;

import android.text.TextUtils;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.common.net.dto.AuthenticateDto;
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

            if (!TextUtils.isEmpty(response.request().header(BuildConfig.Authorization))) {
                return null; // If we already failed with these credentials, don't retry.
            }

            return buildRequest(response.request());
        }
        return null;
    }

    @Override public Response intercept(Chain chain) throws IOException {
        L.i(getClass(), "Adding header");
        return chain.proceed(buildRequest(chain.request()));
    }

    private Request buildRequest(
            Request original
    ) {
        if (!mAuthorizationManager.isAuthenticated()) {
            L.i(getClass(), "it's unauthorized");
            authorize();
            L.i(getClass(), "it's authorized");
        }
        L.i(getClass(), "Original request: " + original);

        return original.newBuilder()
                       .header(BuildConfig.Authorization, mAuthorizationManager.getToken())
                       .header("Accept", "application/json")
                       .method(original.method(), original.body())
                       .build();
    }

    private void authorize() {
        long current = System.currentTimeMillis();
        if (current - lastTimeRefreshed > MAX_REFRESH_STATE) {
            lastTimeRefreshed = current;
            hashCode = generateHashCode();
        }
        new Authorization(hashCode, mAuthorizationManager);
    }

    public static class Authorization {

        @Inject
        OkHttpClient.Builder mHttpClientBuilder;
        @Inject
        Retrofit.Builder mRetrofitBuilder;

        private Authorization(
                long hashCode,
                AuthorizationManager authorizationManager
        ) {
            ApplicationLauncher.mainComponent().inject(this);
            authorize(hashCode, authorizationManager);
        }

        private void authorize(
                long hashCode,
                final AuthorizationManager authorizationManager
        ) {
            Retrofit retrofit = mRetrofitBuilder.client(mHttpClientBuilder.build()).build();
            retrofit.create(UserService.class)
                    .authenticate(hashCode)
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
