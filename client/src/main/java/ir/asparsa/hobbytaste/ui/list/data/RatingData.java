package ir.asparsa.hobbytaste.ui.list.data;

import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.hobbytaste.R;

/**
 * Created by hadi on 12/14/2016 AD.
 */
public class RatingData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.rating;

    private final float mRate;

    public RatingData(float rate) {
        mRate = rate;
    }

    @Override public int getViewType() {
        return VIEW_TYPE;
    }

    public float getRate() {
        return mRate;
    }

    @Override public boolean equals(Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof RatingData)) {
            return false;
        }
        final RatingData other = (RatingData) otherObj;
        return getRate() == other.getRate();
    }
}