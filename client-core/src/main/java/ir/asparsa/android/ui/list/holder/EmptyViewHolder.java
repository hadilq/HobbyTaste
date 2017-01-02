package ir.asparsa.android.ui.list.holder;

import android.os.Bundle;
import android.view.View;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import rx.Observer;

/**
 * @author hadi
 * @since 6/24/2016 AD
 */
public class EmptyViewHolder extends BaseViewHolder {

    public EmptyViewHolder(
            View itemView,
            Observer<BaseRecyclerFragment.Event> observer,
            Bundle savedInstanceState
    ) {
        super(itemView, observer, savedInstanceState);
    }

    @Override
    public void onBindView(BaseRecyclerData data) {

    }

}
