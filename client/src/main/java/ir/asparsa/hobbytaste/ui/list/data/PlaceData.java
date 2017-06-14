package ir.asparsa.hobbytaste.ui.list.data;

import android.os.Parcel;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.database.model.StoreModel;

/**
 * @author hadi
 * @since 12/14/2016 AD.
 */
public class PlaceData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.place;

    private final StoreModel mStoreModel;

    public PlaceData(StoreModel storeModel) {
        mStoreModel = storeModel;
    }

    public StoreModel getStoreModel() {
        return mStoreModel;
    }

    @Override public int getViewType() {
        return VIEW_TYPE;
    }

    @Override public boolean equals(Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof PlaceData)) {
            return false;
        }
        final PlaceData other = (PlaceData) otherObj;
        return (getStoreModel() == null && other.getStoreModel() == null) ||
               (getStoreModel() != null && getStoreModel().equals(other.getStoreModel()));
    }


    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(
            Parcel dest,
            int flags
    ) {
        dest.writeParcelable(this.mStoreModel, flags);
    }

    protected PlaceData(Parcel in) {
        this.mStoreModel = in.readParcelable(StoreModel.class.getClassLoader());
    }

    public static final Creator<PlaceData> CREATOR = new Creator<PlaceData>() {
        @Override public PlaceData createFromParcel(Parcel source) {
            return new PlaceData(source);
        }

        @Override public PlaceData[] newArray(int size) {
            return new PlaceData[size];
        }
    };
}
