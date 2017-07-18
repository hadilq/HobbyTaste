package ir.asparsa.hobbytaste.ui.list.data;

import android.os.Parcel;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.database.model.StoreModel;

/**
 * @author hadi
 */
public class StoreMapData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.store_map;

    private StoreModel store;

    public StoreMapData(StoreModel store) {
        this.store = store;
    }

    public StoreModel getStore() {
        return store;
    }

    @Override public int getViewType() {
        return VIEW_TYPE;
    }

    @Override public boolean equals(Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof StoreMapData)) {
            return false;
        }
        final StoreMapData other = (StoreMapData) otherObj;
        return (getStore() == null && other.getStore() == null) ||
               (getStore() != null && getStore().equals(other.getStore()));
    }


    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(
            Parcel dest,
            int flags
    ) {
        dest.writeParcelable(this.store, flags);
    }

    protected StoreMapData(Parcel in) {
        this.store = in.readParcelable(StoreModel.class.getClassLoader());
    }

    public static final Creator<StoreMapData> CREATOR = new Creator<StoreMapData>() {
        @Override public StoreMapData createFromParcel(Parcel source) {
            return new StoreMapData(source);
        }

        @Override public StoreMapData[] newArray(int size) {
            return new StoreMapData[size];
        }
    };
}
