package ir.asparsa.hobbytaste.core.dagger;

import dagger.Component;
import ir.asparsa.hobbytaste.ui.activity.LaunchActivity;
import ir.asparsa.hobbytaste.ui.fragment.content.MainContentFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.StoreDetailsContentFragment;
import ir.asparsa.hobbytaste.ui.fragment.dialog.SetUsernameDialogFragment;
import ir.asparsa.hobbytaste.ui.fragment.recycler.SettingsRecyclerFragment;
import ir.asparsa.hobbytaste.ui.list.provider.SettingsProvider;
import ir.asparsa.hobbytaste.ui.list.provider.StoreDetailsProvider;

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

    void inject(StoreDetailsProvider storeDetailsProvider);

    void inject(SettingsRecyclerFragment settingsRecyclerFragment);

    void inject(SettingsProvider settingsProvider);

    void inject(SetUsernameDialogFragment setUsernameDialogFragment);

    void inject(StoreDetailsContentFragment storeDetailsContentFragment);
}
