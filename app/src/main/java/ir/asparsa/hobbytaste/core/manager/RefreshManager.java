package ir.asparsa.hobbytaste.core.manager;

import ir.asparsa.hobbytaste.database.model.StoreModel;
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

    public void refreshStores(Subscriber<Collection<StoreModel>> observer) {
        Deque<Subscriber<Collection<StoreModel>>> storesObservers = new ArrayDeque<>();
        storesObservers.add(observer);
        storesObservers.add(mStoresManager.getObserver());
        ConnectableObservable<Collection<StoreModel>> observable = mStoresManager.getRefreshable();
        for (Subscriber<Collection<StoreModel>> storeObserver : storesObservers) {
            observable.subscribe(storeObserver);
        }
        observable.connect();
    }
}
