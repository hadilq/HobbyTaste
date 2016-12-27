package ir.asparsa.hobbytaste.database.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import ir.asparsa.android.core.model.BaseModel;
import ir.asparsa.common.database.model.CommentColumns;
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
    @DatabaseField(columnName = CommentColumns.HASH_CODE)
    private long hashCode;

    public CommentModel() {
    }

    public CommentModel(
            String description,
            float rate,
            long created,
            Long storeId
            long hashCode
    ) {
        this.description = description;
        this.rate = rate;
        this.created = created;
        this.storeId = storeId;
        this.hashCode = hashCode;
    }

    public CommentModel(
            String description,
            long storeId
    ) {
        this.description = description;
        this.storeId = storeId;
        created = System.currentTimeMillis();
        this.hashCode = created ^ (((long) getDescription().hashCode()) << 31);
    }

    public static CommentModel newInstance(StoreCommentDto storeCommentDto) {
        return new CommentModel(storeCommentDto.getDescription(), storeCommentDto.getRate(),
                                storeCommentDto.getCreated(), storeCommentDto.getStoreId(),
                                storeCommentDto.getHashCode());
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

    public long getHashCode() {
        return hashCode;
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
        return (getId() == null && other.getId() == null) ||
               (getId() != null && getId().equals(other.getId()));
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
