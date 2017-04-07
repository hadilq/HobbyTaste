package ir.asparsa.android.ui.list.data;

import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import rx.Observer;

import java.util.Deque;

/**
 * Created by hadi on 12/22/2016 AD.
 */
public abstract class DataObserver implements Observer<BaseRecyclerData>{
    protected Deque<BaseRecyclerData> deque;
    protected int index = 0;

    @Override public void onError(Throwable e) {
    }

    public DataObserver setDeque(Deque<BaseRecyclerData> deque) {
        this.deque = deque;
        return this;
    }
}
