package ir.asparsa.hobbytaste.ui.mvp.presenter;

import android.content.Context;
import com.squareup.picasso.Picasso;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;
import ir.asparsa.hobbytaste.core.manager.StoresManager;
import ir.asparsa.hobbytaste.core.util.MapUtil;
import ir.asparsa.hobbytaste.net.BannerService;
import ir.asparsa.hobbytaste.net.FeedbackService;
import ir.asparsa.hobbytaste.net.StoreService;
import ir.asparsa.hobbytaste.net.UserService;
import ir.asparsa.hobbytaste.ui.mvp.presenter.dagger.DaggerPresenterMockMainComponent;
import ir.asparsa.hobbytaste.ui.mvp.presenter.dagger.PresenterMockAppModule;
import ir.asparsa.hobbytaste.ui.mvp.presenter.dagger.PresenterMockMainComponent;
import ir.asparsa.hobbytaste.ui.mvp.presenter.dagger.PresenterMockNetServiceModule;
import org.junit.Before;
import org.mockito.Mock;

/**
 * @author hadi
 * @since 4/18/2017 AD.
 */
public class BasePresenterTest {

    @Mock
    StoresManager storesManager;
    @Mock
    PreferencesManager preferencesManager;
    @Mock
    MapUtil mapUtil;
    @Mock
    ApplicationLauncher application;
    @Mock
    Context context;
    @Mock
    StoreService storeService;
    @Mock
    UserService userService;
    @Mock
    BannerService bannerService;
    @Mock
    FeedbackService feedbackService;
    @Mock
    Picasso picasso;

    @Before
    public void setup() {
        L.i(getClass(), "Setup is gets called");
        PresenterMockMainComponent component
                = DaggerPresenterMockMainComponent
                .builder()
                .presenterMockAppModule(
                        new PresenterMockAppModule(application, storesManager, preferencesManager, mapUtil))
                .presenterMockNetServiceModule(
                        new PresenterMockNetServiceModule(
                                storeService, userService, bannerService, feedbackService, picasso))
                .build();

        ApplicationLauncher.setTestMainComponent(component);
    }

}
