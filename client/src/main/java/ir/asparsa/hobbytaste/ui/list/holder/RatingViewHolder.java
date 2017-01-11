package ir.asparsa.hobbytaste.ui.list.holder;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.list.data.RatingData;
import rx.Observer;

/**
 * Created by hadi on 12/14/2016 AD.
 */
public class RatingViewHolder extends BaseViewHolder<RatingData> {

    @BindView(R.id.heart)
    ImageView mHeartImageView;
    @BindView(R.id.rate)
    TextView mRateTextView;
    @BindView(R.id.viewed)
    TextView mViewedTextView;

    public RatingViewHolder(
            View itemView,
            Observer<BaseRecyclerFragment.Event> observer,
            Bundle savedInstanceState
    ) {
        super(itemView, observer, savedInstanceState);
        ButterKnife.bind(this, itemView);
    }

    @Override public void onBindView(final RatingData data) {
        mRateTextView.setText(itemView.getContext().getResources()
                                      .getString(R.string.rating, data.getRate(),
                                                 itemView.getContext().getResources().getQuantityString(
                                                         R.plurals.like, (int) data.getRate())));
        mViewedTextView.setText(itemView.getContext().getResources()
                                        .getString(R.string.viewed, data.getViewed()));

        mHeartImageView.getDrawable().setColorFilter(
                itemView.getResources().getColor(data.isLike() ? R.color.heart_on : R.color.heart_off),
                PorterDuff.Mode.SRC_ATOP);
        mHeartImageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (mObserver != null) {
                    mObserver.onNext(new OnHeartClick(data));
                }
            }
        });
    }

    public static class OnHeartClick implements BaseRecyclerFragment.Event {
        private RatingData data;

        OnHeartClick(RatingData data) {
            this.data = data;
        }

        public RatingData getData() {
            return data;
        }
    }

}
