package ir.asparsa.hobbytaste.database.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import ir.asparsa.android.core.model.BaseModel;
import ir.asparsa.common.database.model.Comment;
import ir.asparsa.common.net.dto.CommentProto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
@DatabaseTable(tableName = Comment.TABLE_NAME)
public class CommentModel extends BaseModel<Long> implements Parcelable {


    @DatabaseField(columnName = Comment.Columns.ID, generatedId = true)
    private Long id;

    @DatabaseField(columnName = Comment.Columns.DESCRIPTION, canBeNull = false)
    private String description;
    @DatabaseField(columnName = Comment.Columns.RATE, canBeNull = false)
    private long rate;
    @DatabaseField(columnName = Comment.Columns.LIKE)
    private boolean liked;
    @DatabaseField(columnName = Comment.Columns.CREATOR, canBeNull = false)
    private String creator;
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
            String creator,
            long created,
            long storeId,
            long hashCode
    ) {
        this.description = description;
        this.rate = rate;
        this.liked = like;
        this.creator = creator;
        this.created = created;
        this.storeId = storeId;
        this.hashCode = hashCode;
    }

    public CommentModel(
            String description,
            String creator,
            long storeId
    ) {
        this.description = description;
        this.creator = creator;
        this.storeId = storeId;
        this.created = System.currentTimeMillis();
        this.hashCode = created ^ (((long) getDescription().hashCode()) << 31);
    }

    public static CommentModel newInstance(
            long storeId,
            CommentProto.Comment storeCommentDto
    ) {
        return new CommentModel(storeCommentDto.getDescription(), storeCommentDto.getRate(), storeCommentDto.getLike(),
                                storeCommentDto.getCreator(), storeCommentDto.getCreated(),
                                storeId, storeCommentDto.getHashCode());
    }

    public CommentProto.Comment convertToDto() {
        return CommentProto.Comment
                .newBuilder()
                .setDescription(getDescription())
                .setHashCode(getHashCode())
                .build();
    }

    @Override public Long getId() {
        return id;
    }

    @Override public void setId(Long id) {
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

    public String getCreator() {
        return creator;
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

    public boolean changed(CommentModel comment) {
        return equals(comment) &&
               (getRate() != comment.getRate() || isLiked() != comment.isLiked() ||
                (!TextUtils.isEmpty(getCreator()) && !getCreator().equals(comment.getCreator())));
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
               ", creator='" + creator + '\'' +
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
        dest.writeValue(this.id);
        dest.writeString(this.description);
        dest.writeLong(this.rate);
        dest.writeByte(this.liked ? (byte) 1 : (byte) 0);
        dest.writeString(this.creator);
        dest.writeLong(this.created);
        dest.writeLong(this.storeId);
        dest.writeLong(this.hashCode);
    }

    protected CommentModel(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.description = in.readString();
        this.rate = in.readLong();
        this.liked = in.readByte() != 0;
        this.creator = in.readString();
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
