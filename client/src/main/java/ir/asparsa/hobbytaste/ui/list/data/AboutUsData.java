package ir.asparsa.hobbytaste.ui.list.data;

import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.hobbytaste.R;

/**
 * @author hadi
 * @since 3/16/2017 AD.
 */
public class AboutUsData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.about_us;

    public AboutUsData() {
    }

    @Override public int getViewType() {
        return VIEW_TYPE;
    }

    @Override public boolean equals(Object otherObj) {
        return !((otherObj == null) || !(otherObj instanceof AboutUsData));
    }
}
