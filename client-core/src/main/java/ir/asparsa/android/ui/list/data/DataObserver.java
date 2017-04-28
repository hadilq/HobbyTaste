package ir.asparsa.android.ui.list.data;

import rx.Observer;

import java.util.Deque;

/**
 * @author hadi
 * @since 12/22/2016 AD.
 */
public abstract class DataObserver implements Observer<BaseRecyclerData> {
    protected Deque<BaseRecyclerData> deque;
    protected int index = 0;
    private final long totalElements;

    public DataObserver(long totalElements) {
        this.totalElements = totalElements;
    }

    @Override public void onError(Throwable e) {
    }

    public DataObserver setDeque(Deque<BaseRecyclerData> deque) {
        this.deque = deque;
        return this;
    }

    public long getTotalElements() {
        return totalElements;
    }
}
