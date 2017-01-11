package ir.asparsa.hobbytaste.database.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import ir.asparsa.android.core.model.BaseModel;
import ir.asparsa.common.database.model.Comment;
import ir.asparsa.common.net.dto.StoreCommentDto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
@DatabaseTable(tableName = Comment.TABLE_NAME)
public class CommentModel extends BaseModel implements Parcelable {


    @DatabaseField(columnName = Comment.Columns.ID, generatedId = true)
    private long id;

    @DatabaseField(columnName = Comment.Columns.DESCRIPTION, canBeNull = false)
    private String description;
    @DatabaseField(columnName = Comment.Columns.RATE, canBeNull = false)
    private long rate;
    @DatabaseField(columnName = Comment.Columns.LIKE)
    private boolean liked;
    @DatabaseField(columnName = Comment.Columns.CREATED)
    private long created;
    @DatabaseField(columnName = Comment.Columns.STORE, canBeNull = false)
    private long storeId;
    @DatabaseField(columnName = Comment.Columns.HASH_CODE)
    private long hashCode;


    public CommentModel() {
    }

    public CommentModel(
            String description,
            long rate,
            boolean like,
            long created,
            long storeId,
            long hashCode
    ) {
        this.description = description;
        this.rate = rate;
        this.liked = like;
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
        this.created = System.currentTimeMillis();
        this.hashCode = created ^ (((long) getDescription().hashCode()) << 31);
    }

    public static CommentModel newInstance(StoreCommentDto storeCommentDto) {
        return new CommentModel(storeCommentDto.getDescription(), storeCommentDto.getRate(), storeCommentDto.getLike(),
                                storeCommentDto.getCreated(), storeCommentDto.getStoreId(),
                                storeCommentDto.getHashCode());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public long getRate() {
        return rate;
    }

    public boolean isLiked() {
        return liked;
    }

    public long getCreated() {
        return created;
    }

    public long getHashCode() {
        return hashCode;
    }

    public long getStoreId() {
        return storeId;
    }

    public void heartBeat() {
        liked = !liked;
        rate = liked ? rate + 1 : rate - 1;
    }

    @Override
    public boolean equals(final Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof CommentModel)) {
            return false;
        }
        final CommentModel other = (CommentModel) otherObj;
        return getStoreId() == other.getStoreId() && getHashCode() == other.getHashCode();
    }

    @Override
    public int hashCode() {
        return (int) (getHashCode() >> 15);
    }


    @Override public String toString() {
        return "CommentModel{" +
               "id=" + id +
               ", description='" + description + '\'' +
               ", rate=" + rate +
               ", liked=" + liked +
               ", created=" + created +
               ", storeId=" + storeId +
               ", hashCode=" + hashCode +
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
        dest.writeString(this.description);
        dest.writeLong(this.rate);
        dest.writeByte(this.liked ? (byte) 1 : (byte) 0);
        dest.writeLong(this.created);
        dest.writeLong(this.storeId);
        dest.writeLong(this.hashCode);
    }

    protected CommentModel(Parcel in) {
        this.id = in.readLong();
        this.description = in.readString();
        this.rate = in.readLong();
        this.liked = in.readByte() != 0;
        this.created = in.readLong();
        this.storeId = in.readLong();
        this.hashCode = in.readLong();
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
