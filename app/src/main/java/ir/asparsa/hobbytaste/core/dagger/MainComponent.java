package ir.asparsa.hobbytaste.core.dagger;

import dagger.Component;
import ir.asparsa.hobbytaste.ui.activity.LaunchActivity;
import ir.asparsa.hobbytaste.ui.fragment.content.MainContentFragment;

import javax.inject.Singleton;

/**
 * @author hadi
 * @since 6/26/2016 AD
 */
@Singleton
@Component(modules = {AppModule.class, NetServiceModule.class, DatabaseModule.class})
public interface MainComponent {

    void inject(LaunchActivity launchActivity);

    void inject(MainContentFragment mainContentFragment);
}
