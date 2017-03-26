package ir.asparsa.hobbytaste.core.retrofit;

import android.text.TextUtils;
import com.google.common.net.HttpHeaders;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.common.net.dto.AuthenticateProto;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.BuildConfig;
import ir.asparsa.hobbytaste.core.logger.FirebaseLogger;
import ir.asparsa.hobbytaste.core.manager.AuthorizationManager;
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;
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
import java.util.UUID;

/**
 * @author hadi
 * @since 3/15/2017 AD.
 */
@Singleton
public class AuthorizationFactory implements Authenticator, Interceptor {

    private static final MediaType PROTOBUF = MediaType.parse("application/x-protobuf; charset=utf-8");

    private SecureRandom secureRandom = new SecureRandom();

    private long lastTimeRefreshed;
    private String hash;

    @Inject
    AuthorizationManager mAuthorizationManager;
    @Inject
    PreferencesManager mPreferencesManager;

    @Inject AuthorizationFactory() {
    }

    private String generateHash() {
        return UUID.randomUUID().toString();
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

        HttpUrl originalUrl = original.url();
        String localeQueryParamKey = "locale";
        HttpUrl url = null;
        if (TextUtils.isEmpty(originalUrl.queryParameter(localeQueryParamKey))) {
            url = originalUrl
                    .newBuilder()
                    .addQueryParameter(localeQueryParamKey, LanguageUtil.getLocale(mPreferencesManager).getLanguage())
                    .build();
        }

        Request.Builder builder = original.newBuilder();
        if (url != null) {
            builder.url(url);
        }
        return builder.header(BuildConfig.Authorization, mAuthorizationManager.getToken())
                      .header(HttpHeaders.ACCEPT, PROTOBUF.toString())
                      .header(HttpHeaders.CONTENT_TYPE, PROTOBUF.toString())
                      .method(original.method(), original.body())
                      .build();
    }

    private void authorize(String oldToken) {
        long current = System.currentTimeMillis();
        if (TextUtils.isEmpty(hash)) {
            lastTimeRefreshed = current;
            hash = generateHash();
        }
        if (current - lastTimeRefreshed > BuildConfig.HASH_CODE_EXPIRATION_TIME) {
            lastTimeRefreshed = current;
            hash = generateHash();
        }
        new Authorization(hash, mAuthorizationManager, oldToken);
    }

    public static class Authorization {

        @Inject
        OkHttpClient.Builder mHttpClientBuilder;
        @Inject
        Retrofit.Builder mRetrofitBuilder;

        private Authorization(
                String hash,
                AuthorizationManager authorizationManager,
                String oldToken
        ) {
            ApplicationLauncher.mainComponent().inject(this);
            authorize(hash, authorizationManager, oldToken);
        }

        private void authorize(
                String hash,
                final AuthorizationManager authorizationManager,
                String oldToken
        ) {
            Retrofit retrofit = mRetrofitBuilder.client(mHttpClientBuilder.build()).build();
            AuthenticateProto.Request.Builder builder = AuthenticateProto.Request
                    .newBuilder()
                    .setHash(hash);

            if (!TextUtils.isEmpty(oldToken)) {
                builder.setToken(oldToken);
            }
            retrofit.create(UserService.class)
                    .authenticate(builder.build())
                    .retry(5)
                    .toBlocking()
                    .subscribe(new Observer<AuthenticateProto.Authenticate>() {
                        @Override public void onCompleted() {
                        }

                        @Override public void onError(Throwable e) {
                            L.i(Authorization.class, "Error on authentication!", e);
                            FirebaseLogger.e(e);
                        }

                        @Override public void onNext(AuthenticateProto.Authenticate authenticateDto) {
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
