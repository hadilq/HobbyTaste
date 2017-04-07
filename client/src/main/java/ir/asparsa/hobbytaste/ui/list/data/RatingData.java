package ir.asparsa.hobbytaste.ui.list.data;

import android.os.Parcel;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.hobbytaste.R;

/**
 * @author hadi
 * @since 12/14/2016 AD.
 */
public class RatingData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.rating;

    private long mRate;
    private long mViewed;
    private String description;
    private boolean mLike;
    private boolean showDescription = false;

    public RatingData(
            long mRate,
            long mViewed,
            String description,
            boolean mLike
    ) {
        this.mRate = mRate;
        this.mViewed = mViewed;
        this.description = description;
        this.mLike = mLike;
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

    public String getDescription() {
        return description;
    }

    public boolean isShowDescription() {
        return showDescription;
    }

    public void setShowDescription(boolean showDescription) {
        this.showDescription = showDescription;
    }

    @Override public boolean equals(Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof RatingData)) {
            return false;
        }
        final RatingData other = (RatingData) otherObj;
        return getRate() == other.getRate();
    }


    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(
            Parcel dest,
            int flags
    ) {
        dest.writeLong(this.mRate);
        dest.writeLong(this.mViewed);
        dest.writeString(this.description);
        dest.writeByte(this.mLike ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showDescription ? (byte) 1 : (byte) 0);
    }

    protected RatingData(Parcel in) {
        this.mRate = in.readLong();
        this.mViewed = in.readLong();
        this.description = in.readString();
        this.mLike = in.readByte() != 0;
        this.showDescription = in.readByte() != 0;
    }

    public static final Creator<RatingData> CREATOR = new Creator<RatingData>() {
        @Override public RatingData createFromParcel(Parcel source) {
            return new RatingData(source);
        }

        @Override public RatingData[] newArray(int size) {
            return new RatingData[size];
        }
    };
}
