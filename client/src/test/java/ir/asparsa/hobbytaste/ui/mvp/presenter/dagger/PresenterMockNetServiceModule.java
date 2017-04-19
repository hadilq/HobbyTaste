package ir.asparsa.hobbytaste.ui.mvp.presenter.dagger;

import com.squareup.picasso.Picasso;
import dagger.Module;
import dagger.Provides;
import ir.asparsa.hobbytaste.core.dagger.NetServiceModule;
import ir.asparsa.hobbytaste.net.BannerService;
import ir.asparsa.hobbytaste.net.FeedbackService;
import ir.asparsa.hobbytaste.net.StoreService;
import ir.asparsa.hobbytaste.net.UserService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import javax.inject.Singleton;

/**
 * @author hadi
 * @since 4/18/2017 AD.
 */

@Module
public class PresenterMockNetServiceModule extends NetServiceModule {

    StoreService storeService;
    UserService userService;
    BannerService bannerService;
    FeedbackService feedbackService;
    Picasso picasso;

    public PresenterMockNetServiceModule(
            StoreService storeService,
            UserService userService,
            BannerService bannerService,
            FeedbackService feedbackService,
            Picasso picasso
    ) {
        this.storeService = storeService;
        this.userService = userService;
        this.bannerService = bannerService;
        this.feedbackService = feedbackService;
        this.picasso = picasso;
    }

    @Provides
    @Singleton
    StoreService provideStoreService() {
        return storeService;
    }

    @Provides
    @Singleton
    UserService provideUserService() {
        return userService;
    }

    @Provides
    @Singleton
    BannerService provideBannerService() {
        return bannerService;
    }

    @Provides
    @Singleton
    FeedbackService provideFeedbackService() {
        return feedbackService;
    }

    @Provides
    OkHttpClient.Builder provideOkHttpClientBuilder() {
        return null;
    }

    @Provides
    Retrofit.Builder provideRetrofitBuilder() {
        return null;
    }

    @Provides
    @Singleton
    Picasso providePicasso() {
        return picasso;
    }

    @Provides
    @Singleton
    Retrofit providesRetrofit(
    ) {
        return null;
    }
}
