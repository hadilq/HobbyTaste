package ir.asparsa.hobbytaste.core.dagger;

import dagger.Module;
import dagger.Provides;
import ir.asparsa.hobbytaste.net.StoreService;
import retrofit2.Retrofit;

import javax.inject.Singleton;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@Module
public class NetServiceModule {

    @Provides
    @Singleton
    StoreService provideStoreService(Retrofit retrofit) {
        return retrofit.create(StoreService.class);
    }
}
