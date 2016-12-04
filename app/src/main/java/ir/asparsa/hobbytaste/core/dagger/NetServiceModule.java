package ir.asparsa.hobbytaste.core.dagger;

import android.text.TextUtils;
import dagger.Module;
import dagger.Provides;
import ir.asparsa.hobbytaste.BuildConfig;
import ir.asparsa.hobbytaste.core.logger.L;
import ir.asparsa.hobbytaste.core.manager.AuthorizationManager;
import ir.asparsa.hobbytaste.net.AuthenticateService;
import ir.asparsa.hobbytaste.net.StoreService;
import junit.framework.Assert;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@Module
public class NetServiceModule {

    private static final long CONNECT_TIMEOUT_SECOND = 30;
    private static final long READ_TIMEOUT_SECOND = 30;

    @Provides
    @Singleton
    StoreService provideStoreService(Retrofit retrofit) {
        return retrofit.create(StoreService.class);
    }


    @Provides
    @Singleton
    Retrofit providesRetrofit(final AuthorizationManager authorizationManager) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient
                .connectTimeout(CONNECT_TIMEOUT_SECOND, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECOND, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override public Response intercept(Chain chain) throws IOException {
                        return chain.proceed(buildRequest(chain.request(), authorizationManager));
                    }
                })
                .authenticator(new Authenticator() {
                    @Override public Request authenticate(Route route, Response response) throws IOException {
                        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            authorizationManager.setToken("");
                            return buildRequest(response.request(), authorizationManager);
                        }
                        return null;
                    }
                });

        return getRetrofitBuilder()
                .client(httpClient.build())
                .build();
    }

    private Request buildRequest(Request original, AuthorizationManager authorizationManager) {
        if (!authorizationManager.isAuthenticated()) {
            authorize(authorizationManager);
            if (!authorizationManager.isAuthenticated()) {
                return original;
            }
        }

        return original.newBuilder()
                .header("Authorization", authorizationManager.getToken())
                .header("Accept", "application/json")
                .method(original.method(), original.body())
                .build();
    }

    private void authorize(final AuthorizationManager authorizationManager) {
        Retrofit retrofit = getRetrofitBuilder().build();
        retrofit.create(AuthenticateService.class)
                .authenticate()
                .retry(5)
                .toBlocking()
                .subscribe(new Observer<Map<String, String>>() {
                    @Override public void onCompleted() {
                    }

                    @Override public void onError(Throwable e) {
                        L.i(NetServiceModule.class, "Error on authentication!");
                    }

                    @Override public void onNext(Map<String, String> tokenDto) {
                        String token = tokenDto.get("token");
                        L.i(NetServiceModule.class, "Token: " + token);
                        Assert.assertFalse(TextUtils.isEmpty(token));
                        authorizationManager.setToken(token);
                    }
                });

    }

    private Retrofit.Builder getRetrofitBuilder() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    }
}
