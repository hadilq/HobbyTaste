package ir.asparsa.hobbytaste.database.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import ir.asparsa.android.core.model.BaseModel;
import ir.asparsa.common.database.model.StoreColumns;
import ir.asparsa.common.net.dto.BannerDto;
import ir.asparsa.common.net.dto.StoreDto;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@DatabaseTable(tableName = "stores")
public class StoreModel extends BaseModel implements Parcelable {

    @DatabaseField(id = true, columnName = StoreColumns.ID, canBeNull = false)
    private Long id;

    @DatabaseField(columnName = StoreColumns.LAT, canBeNull = false)
    private Double lat;
    @DatabaseField(columnName = StoreColumns.LON, canBeNull = false)
    private Double lon;

    @DatabaseField(columnName = StoreColumns.TITLE)
    private String title;
    @DatabaseField(columnName = StoreColumns.DESCRIPTION)
    private String description;
    @DatabaseField(columnName = StoreColumns.RATE)
    private float rate;

    private List<BannerModel> banners;

    public StoreModel() {
    }

    public StoreModel(Long id) {
        this.id = id;
    }

    public static StoreModel instantiate(StoreDto storeLightDto) {
        StoreModel storeModel = new StoreModel();
        storeModel.id = storeLightDto.getId();
        storeModel.lat = storeLightDto.getLat();
        storeModel.lon = storeLightDto.getLon();
        storeModel.title = storeLightDto.getTitle();
        storeModel.rate = storeLightDto.getRate();
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

    public Long getId() {
        return id;
    }

    public Double getLon() {
        return lon;
    }

    public Double getLat() {
        return lat;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public float getRate() {
        return rate;
    }

    public List<BannerModel> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerModel> banners) {
        this.banners = banners;
    }

    @Override
    public boolean equals(final Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof StoreModel)) {
            return false;
        }
        final StoreModel other = (StoreModel) otherObj;
        return (((getLat() == null && other.getLat() == null) ||
                 (getLat() != null || getLat().equals(other.getLat()))) &&
                ((getLon() == null && other.getLon() == null) ||
                 (getLon() != null || getLon().equals(other.getLon()))) &&
                ((getId() == null && other.getId() == null) || (getId() != null || getId().equals(other.getId()))));
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getLat()).append(getLon()).append(getId()).toHashCode();
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
        dest.writeValue(this.id);
        dest.writeValue(this.lat);
        dest.writeValue(this.lon);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeFloat(this.rate);
    }

    protected StoreModel(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.lon = (Double) in.readValue(Double.class.getClassLoader());
        this.title = in.readString();
        this.description = in.readString();
        this.rate = in.readFloat();
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
