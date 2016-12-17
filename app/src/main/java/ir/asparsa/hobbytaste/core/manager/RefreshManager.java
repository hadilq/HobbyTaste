package ir.asparsa.hobbytaste.core.manager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
@Singleton
public class RefreshManager {

    @Inject
    StoresManager mStoresManager;

    @Inject
    public RefreshManager() {
    }

    public void refreshStores() {
        mStoresManager.getRefreshable().refresh();
    }

    public interface Refreshable {
        void refresh();
    }

}
