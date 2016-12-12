package ir.asparsa.hobbytaste.core.manager;

import ir.asparsa.common.net.dto.StoreLightDto;
import rx.Subscriber;
import rx.observables.ConnectableObservable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

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

    public void refreshStores(Subscriber<Collection<StoreLightDto>> observer) {
        Deque<Subscriber<Collection<StoreLightDto>>> storesObservers = new ArrayDeque<>();
        storesObservers.add(observer);
        storesObservers.add(mStoresManager.getObserver());
        ConnectableObservable<Collection<StoreLightDto>> observable = mStoresManager.getRefreshable();
        for (Subscriber<Collection<StoreLightDto>> storeObserver : storesObservers) {
            observable.subscribe(storeObserver);
        }
        observable.connect();
    }
}
