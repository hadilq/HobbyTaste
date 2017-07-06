package ir.asparsa.hobbytaste.core.dagger;

import dagger.Component;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.retrofit.AdditionalKeyStoresSSLSocketFactory;
import ir.asparsa.hobbytaste.core.retrofit.AuthorizationFactory;
import ir.asparsa.hobbytaste.core.retrofit.RetrofitException;
import ir.asparsa.hobbytaste.core.route.PlaceRoute;
import ir.asparsa.hobbytaste.core.route.PlacesRoute;
import ir.asparsa.hobbytaste.ui.activity.BaseActivity;
import ir.asparsa.hobbytaste.ui.activity.CrashReportActivity;
import ir.asparsa.hobbytaste.ui.activity.LaunchActivity;
import ir.asparsa.hobbytaste.ui.adapter.NavigationAdapter;
import ir.asparsa.hobbytaste.ui.fragment.ContainerFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.*;
import ir.asparsa.hobbytaste.ui.fragment.dialog.CommentDialogFragment;
import ir.asparsa.hobbytaste.ui.fragment.dialog.SetUsernameDialogFragment;
import ir.asparsa.hobbytaste.ui.fragment.recycler.PlacesRecyclerFragment;
import ir.asparsa.hobbytaste.ui.fragment.recycler.SettingsRecyclerFragment;
import ir.asparsa.hobbytaste.ui.fragment.recycler.StoreDetailsRecyclerFragment;
import ir.asparsa.hobbytaste.ui.list.HorizontalSpaceItemDecoration;
import ir.asparsa.hobbytaste.ui.list.holder.PlaceViewHolder;
import ir.asparsa.hobbytaste.ui.list.holder.StoreMapViewHolder;
import ir.asparsa.hobbytaste.ui.list.provider.PlacesProvider;
import ir.asparsa.hobbytaste.ui.list.provider.SettingsProvider;
import ir.asparsa.hobbytaste.ui.list.provider.StoreDetailsProvider;
import ir.asparsa.hobbytaste.ui.mvp.holder.AddStoreViewHolder;
import ir.asparsa.hobbytaste.ui.mvp.holder.MainContentViewHolder;
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

    void inject(ContainerFragment containerFragment);

    void inject(PlaceRoute placeRoute);

    void inject(SettingsContentFragment settingsContentFragment);

    void inject(AddStoreContentFragment addStoreContentFragment);

    void inject(AddBannerContentFragment addBannerContentFragment);

    void inject(PlacesContentFragment placesContentFragment);

    void inject(PlacesRecyclerFragment placesRecyclerFragment);

    void inject(PlacesProvider placesProvider);

    void inject(PlacesRoute placesRoute);

    void inject(PlaceViewHolder placeViewHolder);

    void inject(MainContentViewHolder mainContentViewHolder);

    void inject(AddStoreViewHolder addStoreViewHolder);

    void inject(NavigationAdapter adapter);

    void inject(HorizontalSpaceItemDecoration horizontalSpaceItemDecoration);

    void inject(BaseContentFragment baseContentFragment);
}
