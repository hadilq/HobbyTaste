package ir.asparsa.hobbytaste.database.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import ir.asparsa.android.core.model.BaseModel;
import ir.asparsa.common.database.model.Store;
import ir.asparsa.common.net.dto.StoreProto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@DatabaseTable(tableName = Store.TABLE_NAME)
public class StoreModel extends BaseModel implements Parcelable {

    @DatabaseField(columnName = Store.Columns.ID, generatedId = true)
    private Long id;

    @DatabaseField(columnName = Store.Columns.LAT, canBeNull = false)
    private double lat;
    @DatabaseField(columnName = Store.Columns.LON, canBeNull = false)
    private double lon;

    @DatabaseField(columnName = Store.Columns.TITLE, canBeNull = false)
    private String title;
    @DatabaseField(columnName = Store.Columns.DESCRIPTION, canBeNull = false)
    private String description;
    @DatabaseField(columnName = Store.Columns.HASH_CODE, canBeNull = false)
    private long hashCode;
    @DatabaseField(columnName = Store.Columns.CREATED, canBeNull = false)
    private long created;
    @DatabaseField(columnName = Store.Columns.CREATOR)
    private String creator;
    @DatabaseField(columnName = Store.Columns.RATE, canBeNull = false)
    private long rate;
    @DatabaseField(columnName = Store.Columns.VIEWED, canBeNull = false)
    private long viewed;
    @DatabaseField(columnName = Store.Columns.LIKE, canBeNull = false)
    private boolean liked;

    private List<BannerModel> banners;

    public StoreModel() {
    }

    public StoreModel(
            double lat,
            double lon,
            @NonNull String title,
            @NonNull String description
    ) {
        this.lat = lat;
        this.lon = lon;
        this.title = title;
        this.description = description;
        this.rate = 0;
        this.viewed = 0;
        this.liked = false;
        this.created = System.currentTimeMillis();
        this.hashCode = (((created ^ (((long) getDescription().hashCode()) << 31)) ^ ((long) getTitle().hashCode())) ^
                         Double.doubleToLongBits(getLat())) ^ Double.doubleToLongBits(getLat());
        banners = new ArrayList<>();
    }

    public static StoreModel instantiate(StoreProto.Store storeDto) {
        StoreModel storeModel = new StoreModel();
        storeModel.lat = storeDto.getLat();
        storeModel.lon = storeDto.getLon();
        storeModel.title = storeDto.getTitle();
        storeModel.rate = storeDto.getRate();
        storeModel.viewed = storeDto.getViewed();
        storeModel.liked = storeDto.getLike();
        storeModel.description = storeDto.getDescription();
        storeModel.hashCode = storeDto.getHashCode();
        storeModel.created = storeDto.getCreated();
        storeModel.creator = storeDto.getCreator();
        List<BannerModel> banners = new ArrayList<>();
        if (storeDto.getBannerCount() != 0) {
            for (StoreProto.Banner bannerDto : storeDto.getBannerList()) {
                banners.add(
                        new BannerModel(bannerDto.getMainUrl(), bannerDto.getThumbnailUrl()));
            }
        }
        storeModel.banners = banners;
        return storeModel;
    }

    public StoreProto.Store convertToDto() {
        StoreProto.Store.Builder builder = StoreProto.Store
                .newBuilder()
                .setLat(lat)
                .setLon(lon)
                .setTitle(title)
                .setDescription(description)
                .setHashCode(hashCode);
        if (this.banners != null && this.banners.size() != 0) {
            for (BannerModel banner : this.banners) {
                builder.addBanner(banner.convertToDto());
            }
        }
        return builder.build();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public long getHashCode() {
        return hashCode;
    }

    public long getCreated() {
        return created;
    }

    public String getCreator() {
        return creator;
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
        if (banners == null) {
            banners = new ArrayList<>();
        }
        return banners;
    }

    public void setBanners(List<BannerModel> banners) {
        this.banners = banners;
    }

    public void heartBeat() {
        liked = !liked;
        rate = liked ? rate + 1 : rate - 1;
    }

    @Override
    public boolean equals(final Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof StoreModel)) {
            return false;
        }
        final StoreModel other = (StoreModel) otherObj;
        return getHashCode() == other.getHashCode();
    }

    @Override public String toString() {
        return "StoreModel{" +
               "id=" + id +
               ", lat=" + lat +
               ", lon=" + lon +
               ", title='" + title + '\'' +
               ", description='" + description + '\'' +
               ", hashCode=" + hashCode +
               ", created=" + created +
               ", creator='" + creator + '\'' +
               ", rate=" + rate +
               ", viewed=" + viewed +
               ", liked=" + liked +
               ", banners=" + banners +
               '}';
    }


    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(
            Parcel dest,
            int flags
    ) {
        dest.writeValue(this.id);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeLong(this.hashCode);
        dest.writeLong(this.created);
        dest.writeString(this.creator);
        dest.writeLong(this.rate);
        dest.writeLong(this.viewed);
        dest.writeByte(this.liked ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.banners);
    }

    protected StoreModel(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.lat = in.readDouble();
        this.lon = in.readDouble();
        this.title = in.readString();
        this.description = in.readString();
        this.hashCode = in.readLong();
        this.created = in.readLong();
        this.creator = in.readString();
        this.rate = in.readLong();
        this.viewed = in.readLong();
        this.liked = in.readByte() != 0;
        this.banners = in.createTypedArrayList(BannerModel.CREATOR);
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
