package ir.asparsa.hobbytaste.ui.list.holder;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.list.data.RatingData;
import rx.functions.Action1;

import java.util.Locale;

/**
 * @author hadi
 * @since 12/14/2016 AD.
 */
public class RatingViewHolder extends BaseViewHolder<RatingData> {

    @BindView(R.id.heart)
    ImageView mHeartImageView;
    @BindView(R.id.arrow)
    ImageView mArrowImageView;
    @BindView(R.id.rate)
    TextView mRateTextView;
    @BindView(R.id.viewed)
    TextView mViewedTextView;
    @BindView(R.id.store_description)
    TextView mDescriptionTextView;
    @BindView(R.id.dashboard)
    ViewGroup mDashboardView;
    @BindView(R.id.rate_tail)
    ImageView mRateTailImageView;
    @BindView(R.id.viewed_tail)
    ImageView mViewedTailImageView;

    private final Drawable mUpArrow;
    private final Drawable mDownArrow;

    public RatingViewHolder(
            View itemView,
            Action1<BaseRecyclerFragment.Event> observer,
            Bundle savedInstanceState
    ) {
        super(itemView, observer, savedInstanceState);
        ButterKnife.bind(this, itemView);

        mUpArrow = itemView.getContext().getResources().getDrawable(R.drawable.up_arrow).mutate();
        mDownArrow = itemView.getContext().getResources().getDrawable(R.drawable.down_arrow).mutate();
        mUpArrow.setColorFilter(itemView.getResources().getColor(R.color.dark_background), PorterDuff.Mode.SRC_ATOP);
        mDownArrow.setColorFilter(itemView.getResources().getColor(R.color.dark_background), PorterDuff.Mode.SRC_ATOP);

        mRateTailImageView.getDrawable().mutate()
                          .setColorFilter(itemView.getResources().getColor(R.color.heart_on), PorterDuff.Mode.SRC_ATOP);
        mViewedTailImageView.getDrawable().mutate()
                            .setColorFilter(
                                    itemView.getResources().getColor(R.color.dark_background),
                                    PorterDuff.Mode.SRC_ATOP);
    }

    @Override public void onBindView(final RatingData data) {
        mRateTextView.setText(String.format(Locale.getDefault(), "%d", data.getRate()));

        mViewedTextView.setText(String.format(Locale.getDefault(), "%d", data.getViewed()));

        mHeartImageView.getDrawable().mutate().setColorFilter(
                itemView.getResources().getColor(data.isLike() ? R.color.heart_on : R.color.heart_off),
                PorterDuff.Mode.SRC_ATOP);
        mHeartImageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (mObserver != null) {
                    mObserver.call(new OnHeartClick(data));
                }
            }
        });

        if (data.isShowDescription()) {
            mArrowImageView.setImageDrawable(mUpArrow);
            mDescriptionTextView.setVisibility(View.VISIBLE);
            mDescriptionTextView.setText(data.getDescription());
        } else {
            mArrowImageView.setImageDrawable(mDownArrow);
            mDescriptionTextView.setVisibility(View.GONE);
        }
        mDashboardView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (mObserver != null) {
                    mObserver.call(new OnArrowClick(data));
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

    public static class OnArrowClick implements BaseRecyclerFragment.Event {
        private RatingData data;

        OnArrowClick(RatingData data) {
            this.data = data;
        }

        public RatingData getData() {
            return data;
        }
    }
}
