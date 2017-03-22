package ir.asparsa.hobbytaste.ui.list.data;

import android.os.Parcel;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.database.model.BannerModel;

import java.util.List;

/**
 * Created by hadi on 12/12/2016 AD.
 */
public class GalleryData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.gallery;

    private List<BannerModel> banners;

    public GalleryData(List<BannerModel> banners) {
        this.banners = banners;
    }

    public List<BannerModel> getBanners() {
        return banners;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override public boolean equals(Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof GalleryData)) {
            return false;
        }
        final GalleryData other = (GalleryData) otherObj;
        return (getBanners() == null && other.getBanners() == null) ||
               (getBanners() != null && getBanners().equals(other.getBanners()));
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(
            Parcel dest,
            int flags
    ) {
        dest.writeTypedList(this.banners);
    }

    protected GalleryData(Parcel in) {
        this.banners = in.createTypedArrayList(BannerModel.CREATOR);
    }

    public static final Creator<GalleryData> CREATOR = new Creator<GalleryData>() {
        @Override public GalleryData createFromParcel(Parcel source) {
            return new GalleryData(source);
        }

        @Override public GalleryData[] newArray(int size) {
            return new GalleryData[size];
        }
    };
}
