package ir.asparsa.android.ui.list.holder;

import android.os.Bundle;
import android.view.View;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;

/**
 * @author hadi
 * @since 6/24/2016 AD
 */
public class EmptyViewHolder extends BaseViewHolder {

    public EmptyViewHolder(
            View itemView,
            BaseRecyclerFragment.OnEventListener onEventListener,
            Bundle savedInstanceState
    ) {
        super(itemView, onEventListener, savedInstanceState);
    }

    @Override
    public void onBindView(BaseRecyclerData data) {

    }

}
