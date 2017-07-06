package ir.asparsa.android.ui.list.data;

import rx.Observer;

import java.util.Deque;

/**
 * @author hadi
 */
public abstract class DataObserver implements Observer<BaseRecyclerData> {
    protected Deque<BaseRecyclerData> deque;
    protected int index = 0;
    private final boolean endOfList;

    public DataObserver(boolean endOfList) {
        this.endOfList = endOfList;
    }

    @Override public void onError(Throwable e) {
    }

    public DataObserver setDeque(Deque<BaseRecyclerData> deque) {
        this.deque = deque;
        return this;
    }

    public boolean isEndOfList() {
        return endOfList;
    }
}
