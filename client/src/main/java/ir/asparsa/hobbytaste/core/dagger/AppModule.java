package ir.asparsa.hobbytaste.core.dagger;

import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import ir.asparsa.hobbytaste.ApplicationLauncher;

import javax.inject.Singleton;

/**
 * @author hadi
 * @since 6/26/2016 AD
 */
@Module
public class AppModule {

    ApplicationLauncher mApplication;

    public AppModule(ApplicationLauncher application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    ApplicationLauncher providesApplicationLauncher() {
        return mApplication;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return mApplication;
    }

}