package ir.asparsa.hobbytaste.ui.list.data;

import android.os.Parcel;
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

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(
            Parcel dest,
            int flags
    ) {
    }

    protected AboutUsData(Parcel in) {
    }

    public static final Creator<AboutUsData> CREATOR = new Creator<AboutUsData>() {
        @Override public AboutUsData createFromParcel(Parcel source) {
            return new AboutUsData(source);
        }

        @Override public AboutUsData[] newArray(int size) {
            return new AboutUsData[size];
        }
    };
}
