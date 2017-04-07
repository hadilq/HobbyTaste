package ir.asparsa.hobbytaste.ui.list.data;

import android.os.Parcel;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.hobbytaste.R;

/**
 * @author hadi
 * @since 12/14/2016 AD.
 */
public class UsernameData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.username;

    private String mUsername;

    public UsernameData(String username) {
        this.mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    @Override public int getViewType() {
        return VIEW_TYPE;
    }

    @Override public boolean equals(Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof UsernameData)) {
            return false;
        }
        final UsernameData other = (UsernameData) otherObj;
        return (getUsername() == null && other.getUsername() == null) ||
               (getUsername() != null && getUsername().equals(other.getUsername()));
    }


    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(
            Parcel dest,
            int flags
    ) {
        dest.writeString(this.mUsername);
    }

    protected UsernameData(Parcel in) {
        this.mUsername = in.readString();
    }

    public static final Creator<UsernameData> CREATOR = new Creator<UsernameData>() {
        @Override public UsernameData createFromParcel(Parcel source) {
            return new UsernameData(source);
        }

        @Override public UsernameData[] newArray(int size) {
            return new UsernameData[size];
        }
    };
}
