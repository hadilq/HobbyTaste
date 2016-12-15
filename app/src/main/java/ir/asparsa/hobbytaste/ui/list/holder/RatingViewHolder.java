package ir.asparsa.hobbytaste.ui.list.holder;

import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.list.data.RatingData;

/**
 * Created by hadi on 12/14/2016 AD.
 */
public class RatingViewHolder extends BaseViewHolder<RatingData> {

    @BindView(R.id.rating)
    RatingBar mRatingBar;

    public RatingViewHolder(
            View itemView,
            BaseRecyclerFragment.OnEventListener onEventListener,
            Bundle savedInstanceState
    ) {
        super(itemView, onEventListener, savedInstanceState);
        ButterKnife.bind(this, itemView);
    }

    @Override public void onBindView(RatingData data) {
        mRatingBar.setRating(data.getRate());
    }
}
