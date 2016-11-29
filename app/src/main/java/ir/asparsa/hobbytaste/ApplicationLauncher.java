package ir.asparsa.hobbytaste;

import android.app.Application;
import ir.asparsa.hobbytaste.core.dagger.AppModule;
import ir.asparsa.hobbytaste.core.dagger.DaggerMainComponent;
import ir.asparsa.hobbytaste.core.dagger.MainComponent;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class ApplicationLauncher extends Application {

    private static MainComponent mMainComponent;

    @Override public void onCreate() {
        super.onCreate();

        LanguageUtil.setupDefaultLocale(this);
        setupDagger();
    }

    private void setupDagger() {
        mMainComponent = DaggerMainComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static MainComponent mainComponent() {
        return mMainComponent;
    }
}
