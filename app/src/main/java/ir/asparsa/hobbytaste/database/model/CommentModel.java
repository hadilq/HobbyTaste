package ir.asparsa.hobbytaste.database.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import ir.asparsa.android.core.model.BaseModel;
import ir.asparsa.common.database.model.CommentColumns;
import ir.asparsa.common.database.model.StoreColumns;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
@DatabaseTable(tableName = "comments")
public class CommentModel extends BaseModel implements Parcelable {


    @DatabaseField(columnName = CommentColumns.ID, id = true, canBeNull = false)
    private Long id;

    @DatabaseField(columnName = CommentColumns.DESCRIPTION)
    private String description;
    @DatabaseField(columnName = CommentColumns.RATE)
    private float rate;
    @DatabaseField(columnName = CommentColumns.CREATED)
    private long created;
    @DatabaseField(columnName = CommentColumns.STORE)
    private Long storeId;
    @DatabaseField(columnName = CommentColumns.CLIENT_CREATED)
    private Long clientCreated;

    public CommentModel() {
    }

    public CommentModel(
            Long id,
            String description,
            float rate,
            long created,
            Long storeId
    ) {
        this.id = id;
        this.description = description;
        this.rate = rate;
        this.created = created;
        this.storeId = storeId;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public float getRate() {
        return rate;
    }

    public long getCreated() {
        return created;
    }

    public Long getClientCreated() {
        return clientCreated;
    }

    public Long getStoreId() {
        return storeId;
    }

    @Override
    public boolean equals(final Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof CommentModel)) {
            return false;
        }
        final CommentModel other = (CommentModel) otherObj;
        return new EqualsBuilder()
                .append(getId(), other.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getClientCreated()).append(getDescription()).append(getId()).toHashCode();
    }

    @Override public String toString() {
        return "CommentModel{" +
               "id=" + id +
               ", description='" + description + '\'' +
               ", rate=" + rate +
               ", storeId=" + storeId +
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
        dest.writeString(this.description);
        dest.writeFloat(this.rate);
        dest.writeValue(this.storeId);
    }

    protected CommentModel(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.description = in.readString();
        this.rate = in.readFloat();
        this.storeId = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Creator<CommentModel> CREATOR = new Creator<CommentModel>() {
        @Override public CommentModel createFromParcel(Parcel source) {
            return new CommentModel(source);
        }

        @Override public CommentModel[] newArray(int size) {
            return new CommentModel[size];
        }
    };
}
