package ir.asparsa.hobbytaste.database.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import ir.asparsa.android.core.model.BaseModel;
import ir.asparsa.common.database.model.StoreColumns;
import ir.asparsa.common.net.dto.BannerDto;
import ir.asparsa.common.net.dto.StoreDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@DatabaseTable(tableName = "stores")
public class StoreModel extends BaseModel implements Parcelable {

    @DatabaseField(id = true, columnName = StoreColumns.ID, canBeNull = false)
    private long id;

    @DatabaseField(columnName = StoreColumns.LAT, canBeNull = false)
    private double lat;
    @DatabaseField(columnName = StoreColumns.LON, canBeNull = false)
    private double lon;

    @DatabaseField(columnName = StoreColumns.TITLE)
    private String title;
    @DatabaseField(columnName = StoreColumns.DESCRIPTION)
    private String description;
    @DatabaseField(columnName = StoreColumns.RATE, canBeNull = false)
    private long rate;
    @DatabaseField(columnName = StoreColumns.VIEWED, canBeNull = false)
    private long viewed;
    @DatabaseField(columnName = StoreColumns.LIKE, canBeNull = false)
    private boolean liked;

    private List<BannerModel> banners;

    public StoreModel() {
    }

    public static StoreModel instantiate(StoreDto storeLightDto) {
        StoreModel storeModel = new StoreModel();
        storeModel.id = storeLightDto.getId();
        storeModel.lat = storeLightDto.getLat();
        storeModel.lon = storeLightDto.getLon();
        storeModel.title = storeLightDto.getTitle();
        storeModel.rate = storeLightDto.getRate();
        storeModel.viewed = storeLightDto.getViewed();
        storeModel.liked = storeLightDto.getLike();
        storeModel.description = storeLightDto.getDescription();
        List<BannerModel> banners = new ArrayList<>();
        if (storeLightDto.getBanners() != null && storeLightDto.getBanners().size() != 0) {
            for (BannerDto bannerDto : storeLightDto.getBanners()) {
                banners.add(
                        new BannerModel(bannerDto.getMainUrl(), bannerDto.getThumbnailUrl(), storeLightDto.getId()));
            }
        }
        storeModel.banners = banners;
        return storeModel;
    }

    public long getId() {
        return id;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getRate() {
        return rate;
    }

    public long getViewed() {
        return viewed;
    }

    public boolean isLiked() {
        return liked;
    }

    public List<BannerModel> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerModel> banners) {
        this.banners = banners;
    }

    public void heartBeat() {
        liked = !liked;
    }

    @Override
    public boolean equals(final Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof StoreModel)) {
            return false;
        }
        final StoreModel other = (StoreModel) otherObj;
        return (getLat() == other.getLat() &&
                getLon() == other.getLon() &&
                getId() == other.getId());
    }

    @Override
    public int hashCode() {
        long lat = Double.doubleToLongBits(getLat());
        long lon = Double.doubleToLongBits(getLon());
        return (int) (((lat ^ lat >> 31) ^ (lon ^ lon >> 31)) ^ (getId() ^ getId() >> 31));
    }

    @Override public String toString() {
        return "StoreModel{" +
               "id=" + id +
               ", lat=" + lat +
               ", lon=" + lon +
               ", title='" + title + '\'' +
               ", description='" + description + '\'' +
               ", rate=" + rate +
               '}';
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(
            Parcel dest,
            int flags
    ) {
        dest.writeLong(this.id);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeValue(this.rate);
        dest.writeValue(this.viewed);
        dest.writeByte(this.liked ? (byte) 1 : (byte) 0);
    }

    protected StoreModel(Parcel in) {
        this.id = in.readLong();
        this.lat = in.readDouble();
        this.lon = in.readDouble();
        this.title = in.readString();
        this.description = in.readString();
        this.rate = (Long) in.readValue(Long.class.getClassLoader());
        this.viewed = (Long) in.readValue(Long.class.getClassLoader());
        this.liked = in.readByte() != 0;
    }

    public static final Creator<StoreModel> CREATOR = new Creator<StoreModel>() {
        @Override public StoreModel createFromParcel(Parcel source) {
            return new StoreModel(source);
        }

        @Override public StoreModel[] newArray(int size) {
            return new StoreModel[size];
        }
    };
}
