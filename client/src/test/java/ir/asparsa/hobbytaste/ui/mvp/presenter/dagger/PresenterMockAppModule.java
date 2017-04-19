package ir.asparsa.hobbytaste.ui.mvp.presenter.dagger;

import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.dagger.AppModule;
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;
import ir.asparsa.hobbytaste.core.manager.StoresManager;
import ir.asparsa.hobbytaste.core.util.MapUtil;

import javax.inject.Singleton;

/**
 * @author hadi
 * @since 4/18/2017 AD.
 */

@Module
public class PresenterMockAppModule extends AppModule {

    StoresManager storesManager;
    PreferencesManager preferencesManager;
    MapUtil mapUtil;
    ApplicationLauncher application;

    public PresenterMockAppModule(
            ApplicationLauncher application,
            StoresManager storesManager,
            PreferencesManager preferencesManager,
            MapUtil mapUtil
    ) {
        super(application);
        this.storesManager = storesManager;
        this.preferencesManager = preferencesManager;
        this.mapUtil = mapUtil;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    ApplicationLauncher providesApplicationLauncher() {
        return application;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return application;
    }

    @Provides
    @Singleton
    public StoresManager provideStoresManager() {
        return storesManager;
    }

    @Provides
    @Singleton
    public PreferencesManager providePreferencesManager() {
        return preferencesManager;
    }

    @Provides
    @Singleton
    public MapUtil provideMapUtil() {
        return mapUtil;
    }
}
