package ir.asparsa.hobbytaste.ui.mvp.presenter.dagger;

import dagger.Component;
import ir.asparsa.hobbytaste.core.dagger.MainComponent;

import javax.inject.Singleton;

/**
 * @author hadi
 * @since 4/18/2017 AD.
 */
@Singleton
@Component(modules = {PresenterMockAppModule.class, PresenterMockNetServiceModule.class,
                      PresenterMockDatabaseModule.class})
public interface PresenterMockMainComponent extends MainComponent {
}
