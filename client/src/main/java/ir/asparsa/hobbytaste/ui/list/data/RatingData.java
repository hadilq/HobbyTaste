package ir.asparsa.hobbytaste.ui.list.data;

import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.hobbytaste.R;

/**
 * Created by hadi on 12/14/2016 AD.
 */
public class RatingData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.rating;

    private long mRate;
    private long mViewed;
    private boolean mLike;

    public RatingData(
            long rate,
            long viewed,
            boolean like
    ) {
        this.mRate = rate;
        this.mViewed = viewed;
        this.mLike = like;
    }

    @Override public int getViewType() {
        return VIEW_TYPE;
    }

    public long getRate() {
        return mRate;
    }

    public long getViewed() {
        return mViewed;
    }

    public boolean isLike() {
        return mLike;
    }

    public void setRate(long mRate) {
        this.mRate = mRate;
    }

    public void setViewed(long mViewed) {
        this.mViewed = mViewed;
    }

    public void setLike(boolean mLike) {
        this.mLike = mLike;
    }

    @Override public boolean equals(Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof RatingData)) {
            return false;
        }
        final RatingData other = (RatingData) otherObj;
        return getRate() == other.getRate();
    }
}
