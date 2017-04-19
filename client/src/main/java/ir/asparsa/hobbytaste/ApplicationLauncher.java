package ir.asparsa.hobbytaste;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.core.dagger.AppModule;
import ir.asparsa.hobbytaste.core.dagger.DaggerMainComponent;
import ir.asparsa.hobbytaste.core.dagger.MainComponent;
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;
import ir.asparsa.hobbytaste.core.util.UncaughtExceptionHandler;

import javax.inject.Inject;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class ApplicationLauncher extends Application {

    private static MainComponent sMainComponent;

    @Inject
    PreferencesManager mPreferencesManager;

    @Override public void onCreate() {
        super.onCreate();
        L.i(getClass(), "Application created");
        initialize();
    }

    private void initialize() {
        sMainComponent = setupDagger();
        sMainComponent.inject(this);
        LanguageUtil.setupDefaultLocale(mPreferencesManager, this);

        Thread.setDefaultUncaughtExceptionHandler(
                new UncaughtExceptionHandler(new UncaughtExceptionHandler.UncaughtExceptionController(
                        Thread.getDefaultUncaughtExceptionHandler()), this));
    }

    private MainComponent setupDagger() {
        return DaggerMainComponent.builder()
                                  .appModule(new AppModule(this))
                                  .build();
    }

    public static MainComponent mainComponent() {
        return sMainComponent;
    }

    public static void setTestMainComponent(MainComponent testMainComponent) {
        sMainComponent = testMainComponent;
    }

    @Override protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
