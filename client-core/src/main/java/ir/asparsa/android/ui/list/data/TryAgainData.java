package ir.asparsa.android.ui.list.data;


import android.os.Parcel;
import ir.asparsa.android.R;

/**
 * @author hadi
 * @since 7/25/2016 AD
 */
public class TryAgainData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.try_again;

    private boolean mStart;
    private String mErrorMessage;

    public TryAgainData(
            boolean s
    ) {
        this.mStart = s;
    }

    public void setErrorMessage(String mErrorMessage) {
        this.mErrorMessage = mErrorMessage;
    }

    public boolean start() {
        return mStart;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    @Override public int getViewType() {
        return VIEW_TYPE;
    }

    @Override public boolean equals(Object otherObj) {
        return !((otherObj == null) || !(otherObj instanceof TryAgainData));
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(
            Parcel dest,
            int flags
    ) {
        dest.writeByte(this.mStart ? (byte) 1 : (byte) 0);
        dest.writeString(this.mErrorMessage);
    }

    protected TryAgainData(Parcel in) {
        this.mStart = in.readByte() != 0;
        this.mErrorMessage = in.readString();
    }

    public static final Creator<TryAgainData> CREATOR = new Creator<TryAgainData>() {
        @Override public TryAgainData createFromParcel(Parcel source) {
            return new TryAgainData(source);
        }

        @Override public TryAgainData[] newArray(int size) {
            return new TryAgainData[size];
        }
    };
}
