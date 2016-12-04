package ir.asparsa.hobbytaste.database.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import ir.asparsa.hobbytaste.core.model.BaseModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@DatabaseTable(tableName = "stores")
public class StoreModel extends BaseModel implements Parcelable {
    public interface Columns {
        String ID = "id";
        String LAT = "lat";
        String LON = "lon";
        String TITLE = "title";
        String DESCRIPTION = "description";
        String RATE = "rate";
        String BANNER_URL = "banner";
        String ICON_URL = "icon";
    }

    @DatabaseField(columnName = Columns.ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = Columns.LAT, canBeNull = false)
    private Double lat;
    @DatabaseField(columnName = Columns.LON, canBeNull = false)
    private Double lon;

    @DatabaseField(columnName = Columns.TITLE)
    private String title;
    @DatabaseField(columnName = Columns.DESCRIPTION)
    private String description;
    @DatabaseField(columnName = Columns.RATE)
    private float rate;
    @DatabaseField(columnName = Columns.BANNER_URL)
    private String bannerUrl;
    @DatabaseField(columnName = Columns.ICON_URL)
    private String iconUrl;

    public StoreModel() {
    }

    public int getId() {
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

    public String getBannerUrl() {
        return bannerUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }


    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeValue(this.lon);
        dest.writeValue(this.lat);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeFloat(this.rate);
        dest.writeString(this.bannerUrl);
        dest.writeString(this.iconUrl);
    }

    protected StoreModel(Parcel in) {
        this.id = in.readInt();
        this.lon = (Double) in.readValue(Double.class.getClassLoader());
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.title = in.readString();
        this.description = in.readString();
        this.rate = in.readFloat();
        this.bannerUrl = in.readString();
        this.iconUrl = in.readString();
    }

    public static final Creator<StoreModel> CREATOR = new Creator<StoreModel>() {
        @Override public StoreModel createFromParcel(Parcel source) {
            return new StoreModel(source);
        }

        @Override public StoreModel[] newArray(int size) {
            return new StoreModel[size];
        }
    };

    @Override
    public boolean equals(final Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof StoreModel)) {
            return false;
        }
        final StoreModel other = (StoreModel) otherObj;
        return new EqualsBuilder()
                .append(getLat(), other.getLat())
                .append(getLon(), other.getLon())
                .append(getId(), other.getId())
                .isEquals();
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
               ", bannerUrl='" + bannerUrl + '\'' +
               ", iconUrl='" + iconUrl + '\'' +
               '}';
    }
}
