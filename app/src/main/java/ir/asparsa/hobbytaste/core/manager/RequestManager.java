package ir.asparsa.hobbytaste.core.manager;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import rx.Observable;
import rx.Subscription;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@Singleton
public class RequestManager {

    Map<String, Request<?>> mRequests = new ArrayMap<>();

    @Inject
    public RequestManager() {
    }

    public void put(@NonNull Class<?> clazz, @NonNull Request<?> value) {
        value.unsubscribe();
        mRequests.put(clazz.getName(), value);
    }

    @Nullable
    public Request<?> get(@NonNull Class<?> clazz) {
        String key = clazz.getName();
        if (mRequests.containsKey(key)) {
            Request<?> observable = mRequests.get(key);
            mRequests.remove(observable);
            return observable;
        }
        return null;
    }

    public static class Request<T> {
        private Observable<T> mRequestObservable;
        private Subscription mRequestSubscription;
        private boolean received = false;

        public Request(Observable<T> requestObservable) {
            this.mRequestObservable = requestObservable;
        }

        public void unsubscribe() {
            mRequestSubscription.unsubscribe();
            mRequestSubscription = null;
        }

        public void setRequestSubscription(Subscription requestSubscription) {
            this.mRequestSubscription = requestSubscription;
        }

        public Observable<T> getRequestObservable() {
            return mRequestObservable;
        }

        public void setRequestObservable(Observable<T> requestObservable) {
            this.mRequestObservable = requestObservable;
        }

        public boolean isReceived() {
            return received;
        }

        public void received() {
            received = true;
        }
    }


}
