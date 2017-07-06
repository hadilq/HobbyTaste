package ir.asparsa.hobbytaste.database.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import ir.asparsa.android.core.model.BaseModel;
import ir.asparsa.common.database.model.Banner;
import ir.asparsa.common.net.dto.StoreProto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
@DatabaseTable(tableName = Banner.TABLE_NAME)
public class BannerModel extends BaseModel<Long> implements Parcelable {

    @DatabaseField(columnName = Banner.Columns.ID, generatedId = true)
    private Long id;

    @DatabaseField(columnName = Banner.Columns.MAIN_URL)
    private String mainUrl;
    @DatabaseField(columnName = Banner.Columns.THUMBNAIL_URL)
    private String thumbnailUrl;
    @DatabaseField(columnName = Banner.Columns.STORE)
    private Long storeId;

    public BannerModel() {
    }

    public BannerModel(
            String mainUrl,
            String thumbnailUrl
    ) {
        this.mainUrl = mainUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public StoreProto.Banner convertToDto() {
        return StoreProto.Banner
                .newBuilder()
                .setMainUrl(mainUrl)
                .setThumbnailUrl(thumbnailUrl)
                .build();
    }

    @Override public Long getId() {
        return id;
    }

    @Override public void setId(Long id) {
        this.id = id;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    @Override
    public boolean equals(final Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof BannerModel)) {
            return false;
        }
        final BannerModel other = (BannerModel) otherObj;
        return ((getMainUrl() == null && other.getMainUrl() == null) ||
                (getMainUrl() != null && getMainUrl().equals(other.getMainUrl()))) &&
               ((getThumbnailUrl() == null && other.getThumbnailUrl() == null) ||
                (getThumbnailUrl() != null && getThumbnailUrl().equals(other.getThumbnailUrl()))) &&
               ((getStoreId() == null && other.getStoreId() == null) ||
                (getStoreId() != null && getStoreId().equals(other.getStoreId())));
    }

    @Override public int hashCode() {
        return ((getMainUrl() == null ? 0 : getMainUrl().hashCode()) ^
                (getThumbnailUrl() == null ? 0 : getThumbnailUrl().hashCode())) ^ (int) ((getId() ^ getId() >> 31));
    }

    @Override public String toString() {
        return "BannerModel{" +
               "id=" + id +
               ", mainUrl='" + mainUrl + '\'' +
               ", thumbnailUrl='" + thumbnailUrl + '\'' +
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
        dest.writeString(this.mainUrl);
        dest.writeString(this.thumbnailUrl);
        dest.writeValue(this.storeId);
    }

    protected BannerModel(Parcel in) {
        this.id = in.readLong();
        this.mainUrl = in.readString();
        this.thumbnailUrl = in.readString();
        this.storeId = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Creator<BannerModel> CREATOR = new Creator<BannerModel>() {
        @Override public BannerModel createFromParcel(Parcel source) {
            return new BannerModel(source);
        }

        @Override public BannerModel[] newArray(int size) {
            return new BannerModel[size];
        }
    };
}
