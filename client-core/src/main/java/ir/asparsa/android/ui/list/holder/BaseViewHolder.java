package ir.asparsa.android.ui.list.holder;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import rx.Observer;

/**
 * @author hadi
 * @since 6/24/2016 AD
 */
public abstract class BaseViewHolder<DATA extends BaseRecyclerData> extends RecyclerView.ViewHolder {

    protected final Observer mObserver;

    public BaseViewHolder(
            View itemView,
            Observer observer,
            Bundle savedInstanceState
    ) {
        super(itemView);
        mObserver = observer;
    }

    public abstract void onBindView(DATA data);

    public void onResume() {
    }

    public void onStart() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onDestroy() {
    }

    public void onLowMemory() {
    }
}
