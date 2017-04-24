package ir.asparsa.hobbytaste.core.dagger;

import dagger.Component;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.retrofit.AdditionalKeyStoresSSLSocketFactory;
import ir.asparsa.hobbytaste.core.retrofit.AuthorizationFactory;
import ir.asparsa.hobbytaste.core.retrofit.RetrofitException;
import ir.asparsa.hobbytaste.ui.activity.BaseActivity;
import ir.asparsa.hobbytaste.ui.activity.CrashReportActivity;
import ir.asparsa.hobbytaste.ui.activity.LaunchActivity;
import ir.asparsa.hobbytaste.ui.fragment.content.AddBannerContentFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.MainContentFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.StoreDetailsContentFragment;
import ir.asparsa.hobbytaste.ui.fragment.dialog.CommentDialogFragment;
import ir.asparsa.hobbytaste.ui.fragment.dialog.SetUsernameDialogFragment;
import ir.asparsa.hobbytaste.ui.fragment.recycler.SettingsRecyclerFragment;
import ir.asparsa.hobbytaste.ui.fragment.recycler.StoreDetailsRecyclerFragment;
import ir.asparsa.hobbytaste.ui.list.holder.StoreMapViewHolder;
import ir.asparsa.hobbytaste.ui.list.provider.SettingsProvider;
import ir.asparsa.hobbytaste.ui.list.provider.StoreDetailsProvider;
import ir.asparsa.hobbytaste.ui.mvp.presenter.AddBannerPresenter;
import ir.asparsa.hobbytaste.ui.mvp.presenter.StorePresenter;

import javax.inject.Singleton;

/**
 * @author hadi
 * @since 6/26/2016 AD
 */
@Singleton
@Component(modules = {AppModule.class, NetServiceModule.class, DatabaseModule.class})
public interface MainComponent {

    void inject(ApplicationLauncher applicationLauncher);

    void inject(BaseActivity baseActivity);

    void inject(LaunchActivity launchActivity);

    void inject(CrashReportActivity crashReportActivity);

    void inject(MainContentFragment mainContentFragment);

    void inject(StoreDetailsProvider storeDetailsProvider);

    void inject(SettingsRecyclerFragment settingsRecyclerFragment);

    void inject(SettingsProvider settingsProvider);

    void inject(SetUsernameDialogFragment setUsernameDialogFragment);

    void inject(StoreDetailsContentFragment storeDetailsContentFragment);

    void inject(CommentDialogFragment commentDialogFragment);

    void inject(StoreDetailsRecyclerFragment storeDetailsRecyclerFragment);

    void inject(RetrofitException retrofitException);

    void inject(AdditionalKeyStoresSSLSocketFactory additionalKeyStoresSSLSocketFactory);

    void inject(AuthorizationFactory.Authorization authorization);

    void inject(StorePresenter storePresenter);

    void inject(StoreMapViewHolder storeMapViewHolder);

    void inject(AddBannerPresenter addBannerPresenter);
}
