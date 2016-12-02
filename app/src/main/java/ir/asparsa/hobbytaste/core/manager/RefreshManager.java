package ir.asparsa.hobbytaste.core.manager;

import ir.asparsa.hobbytaste.database.model.StoreModel;
import rx.Observable;
import rx.Observer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayDeque;
import java.util.Collection;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
@Singleton
public class RefreshManager {

    private final StoresManager mStoresManager;
    private final Collection<Observer<Collection<StoreModel>>> mStoreObservers = new ArrayDeque<>();

    @Inject
    public RefreshManager(StoresManager storesManager) {
        mStoresManager = storesManager;
        addStoresObserver(mStoresManager.getObserver());
    }

    public void addStoresObserver(Observer<Collection<StoreModel>> observer) {
        mStoreObservers.add(observer);
    }

    public void refreshStores() {
        Observable<Collection<StoreModel>> observable = mStoresManager.getRefreshable();
        for (Observer<Collection<StoreModel>> storeObserver : mStoreObservers) {
            observable.subscribe(storeObserver);
        }
    }
}
