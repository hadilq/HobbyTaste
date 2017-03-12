package ir.asparsa.hobbytaste.core.dagger;

import android.content.Context;
import android.text.TextUtils;
import dagger.Module;
import dagger.Provides;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.common.net.dto.AuthenticateDto;
import ir.asparsa.hobbytaste.BuildConfig;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.manager.AuthorizationManager;
import ir.asparsa.hobbytaste.core.retrofit.AdditionalKeyStoresSSLSocketFactory;
import ir.asparsa.hobbytaste.core.retrofit.RxErrorHandlingCallAdapterFactory;
import ir.asparsa.hobbytaste.net.BannerService;
import ir.asparsa.hobbytaste.net.StoreService;
import ir.asparsa.hobbytaste.net.UserService;
import junit.framework.Assert;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;

import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;
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
    UserService provideUserService(Retrofit retrofit) {
        return retrofit.create(UserService.class);
    }

    @Provides
    @Singleton
    BannerService provideBannerService(Retrofit retrofit) {
        return retrofit.create(BannerService.class);
    }

    @Provides
    @Singleton
    Retrofit providesRetrofit(
            final AuthorizationManager authorizationManager,
            final Context context
    ) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient
                .connectTimeout(CONNECT_TIMEOUT_SECOND, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECOND, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override public Response intercept(Chain chain) throws IOException {
                        return chain.proceed(buildRequest(chain.request(), authorizationManager, context));
                    }
                })
                .authenticator(new Authenticator() {
                    @Override public Request authenticate(
                            Route route,
                            Response response
                    ) throws IOException {
                        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            authorizationManager.setToken("");
                            return buildRequest(response.request(), authorizationManager, context);
                        }
                        return null;
                    }
                });

        setupClientEssentials(httpClient, context);


        return getRetrofitBuilder()
                .client(httpClient.build())
                .build();
    }

    private Request buildRequest(
            Request original,
            AuthorizationManager authorizationManager,
            Context context
    ) {
        if (!authorizationManager.isAuthenticated()) {
            authorize(authorizationManager, context);
            if (!authorizationManager.isAuthenticated()) {
                return original;
            }
        }

        return original.newBuilder()
                       .header(BuildConfig.Authorization, authorizationManager.getToken())
                       .header("Accept", "application/json")
                       .method(original.method(), original.body())
                       .build();
    }

    private void authorize(
            final AuthorizationManager authorizationManager,
            Context context
    ) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        setupClientEssentials(httpClient, context);
        Retrofit retrofit = getRetrofitBuilder().client(httpClient.build()).build();
        retrofit.create(UserService.class)
                .authenticate()
                .retry(5)
                .toBlocking()
                .subscribe(new Observer<AuthenticateDto>() {
                    @Override public void onCompleted() {
                    }

                    @Override public void onError(Throwable e) {
                        L.i(NetServiceModule.class, "Error on authentication!", e);
                    }

                    @Override public void onNext(AuthenticateDto authenticateDto) {
                        String token = authenticateDto.getToken();
                        L.i(NetServiceModule.class, "Token: " + token);
                        Assert.assertFalse(TextUtils.isEmpty(token));
                        authorizationManager.setToken(token);
                        authorizationManager.setUsername(authenticateDto.getUsername());
                    }
                });

    }

    private void setupClientEssentials(
            OkHttpClient.Builder httpClient,
            Context context
    ) {
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggerInterceptor = new HttpLoggingInterceptor();
            loggerInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(loggerInterceptor);
        }

        if (BuildConfig.PRODUCT) {
            setupSslSocket(httpClient, context);
        }

    }

    private Retrofit.Builder getRetrofitBuilder() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create());
    }

    private void setupSslSocket(
            OkHttpClient.Builder httpClient,
            Context context
    ) {
        try {
            KeyStore ksTrust = KeyStore.getInstance("BKS");
            InputStream inputStream = context.getResources().openRawResource(R.raw.mystore);
            try {
                ksTrust.load(inputStream, "secret".toCharArray());
            } finally {
                inputStream.close();
            }

            if (BuildConfig.DEBUG) {
                Enumeration enumeration = ksTrust.aliases();
                while (enumeration.hasMoreElements()) {
                    String alias = (String) enumeration.nextElement();
                    System.out.println("alias name: " + alias);
                    Certificate certificate = ksTrust.getCertificate(alias);
                    System.out.println(certificate.toString());
                }
            }

            AdditionalKeyStoresSSLSocketFactory sslSocketFactory = new AdditionalKeyStoresSSLSocketFactory(ksTrust);
            httpClient.sslSocketFactory(sslSocketFactory, sslSocketFactory.getTrustManager());

            httpClient.hostnameVerifier(new HostnameVerifier() {
                @Override public boolean verify(
                        String hostname,
                        SSLSession session
                ) {
                    return BuildConfig.HOSTNAME.equalsIgnoreCase(hostname);
                }
            });
        } catch (IOException | GeneralSecurityException e) {
            L.w(NetServiceModule.class, "Cannot setup ssl socket", e);
        }
    }
}
