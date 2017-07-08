package ir.asparsa.hobbytaste.core.dagger;

import android.content.Context;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.BuildConfig;
import ir.asparsa.hobbytaste.core.retrofit.AdditionalKeyStoresSSLSocketFactory;
import ir.asparsa.hobbytaste.core.retrofit.AuthorizationFactory;
import ir.asparsa.hobbytaste.core.retrofit.RxErrorHandlingCallAdapterFactory;
import ir.asparsa.hobbytaste.net.BannerService;
import ir.asparsa.hobbytaste.net.FeedbackService;
import ir.asparsa.hobbytaste.net.StoreService;
import ir.asparsa.hobbytaste.net.UserService;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.protobuf.ProtoConverterFactory;

import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.security.GeneralSecurityException;
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
    FeedbackService provideFeedbackService(Retrofit retrofit) {
        return retrofit.create(FeedbackService.class);
    }

    @Provides
    OkHttpClient.Builder provideOkHttpClientBuilder() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggerInterceptor = new HttpLoggingInterceptor();
            loggerInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(loggerInterceptor);
        }

        if (BuildConfig.PRODUCT) {
            try {
                AdditionalKeyStoresSSLSocketFactory sslSocketFactory = new AdditionalKeyStoresSSLSocketFactory();
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

        return httpClient;
    }

    @Provides
    Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(ProtoConverterFactory.create())
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create());
    }

    @Provides
    @Singleton
    Picasso providePicasso(
            Context context,
            OkHttpClient.Builder httpClient
    ) {
        httpClient.cache(new Cache(context.getCacheDir(), Integer.MAX_VALUE));
        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(httpClient.build()))
                .build();
    }

    @Provides
    @Singleton
    Retrofit providesRetrofit(
            final OkHttpClient.Builder httpClient,
            Retrofit.Builder retrofitBuilder,
            AuthorizationFactory authenticator,
            Picasso picasso
    ) {
        // First install Picasso
        Picasso.setSingletonInstance(picasso);

        // Then create retrofit
        httpClient
                .connectTimeout(CONNECT_TIMEOUT_SECOND, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECOND, TimeUnit.SECONDS)
                .addInterceptor(authenticator)
                .authenticator(authenticator);

        return retrofitBuilder
                .client(httpClient.build())
                .build();
    }
}
