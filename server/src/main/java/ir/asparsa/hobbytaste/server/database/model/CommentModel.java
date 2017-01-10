package ir.asparsa.hobbytaste.server.database.model;

import ir.asparsa.common.database.model.Comment;
import ir.asparsa.common.net.dto.StoreCommentDto;

import javax.persistence.*;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
@Entity
@Table(name = Comment.TABLE_NAME)
public class CommentModel {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = Comment.Columns.DESCRIPTION)
    private String description;
    @Column(name = Comment.Columns.RATE)
    private Long rate;
    @Column(name = Comment.Columns.CREATED)
    private long created;
    @Column(name = Comment.Columns.HASH_CODE)
    private long hashCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Comment.Columns.STORE)
    private StoreModel store;

    public CommentModel() {
    }

    public CommentModel(
            String description,
            long hashCode,
            StoreModel store
    ) {
        this.description = description;
        this.hashCode = hashCode;
        this.store = store;
        this.rate = 0L;
        this.created = System.currentTimeMillis();
    }

    public StoreCommentDto convertToDto(
            boolean like
    ) {
        return new StoreCommentDto(getDescription(), getCreated(), getStore().getId(), getHashCode(), getRate(), like);
    }

    public static CommentModel newInstance(
            StoreCommentDto comment,
            StoreModel store
    ) {
        return new CommentModel(comment.getDescription(), comment.getHashCode(), store);
    }

    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Long getRate() {
        return rate;
    }

    public Long increaseRate() {
        return ++rate;
    }

    public Long decreaseRate() {
        return --rate;
    }

    public StoreModel getStore() {
        return store;
    }

    public long getCreated() {
        return created;
    }

    public long getHashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(final Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof CommentModel)) {
            return false;
        }
        final CommentModel other = (CommentModel) otherObj;
        return store.getId() == store.getId() && getHashCode() == other.getHashCode();
    }

    @Override
    public int hashCode() {
        return (int) (hashCode >> 15);
    }

    @Override public String toString() {
        return "CommentModel{" +
               "id=" + id +
               ", description='" + description + '\'' +
               ", rate=" + rate +
               ", created=" + created +
               ", hashCode=" + hashCode +
               ", storeId=" + store.getId() +
               '}';
    }
}
