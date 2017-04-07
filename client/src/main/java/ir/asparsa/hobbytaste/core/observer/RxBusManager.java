package ir.asparsa.hobbytaste.core.observer;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author hadi
 * @since 7/5/2016 AD
 */
@Singleton
public class RxBusManager {

    @Inject
    public RxBusManager() {
    }

    private final Subject<Object, Object> mBus = new SerializedSubject<>(PublishSubject.create());

    public void send(Object o) {
        mBus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return mBus;
    }
}
