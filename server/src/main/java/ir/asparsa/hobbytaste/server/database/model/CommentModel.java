package ir.asparsa.hobbytaste.server.database.model;

import ir.asparsa.common.database.model.CommentColumns;
import ir.asparsa.common.net.dto.StoreCommentDto;

import javax.persistence.*;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
@Entity
@Table(name = "comments")
public class CommentModel {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = CommentColumns.DESCRIPTION)
    private String description;
    @Column(name = CommentColumns.RATE)
    private float rate;
    @Column(name = CommentColumns.CREATED)
    private long created;
    @Column(name = CommentColumns.HASH_CODE)
    private long hashCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = CommentColumns.STORE)
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
        this.created = System.currentTimeMillis();
    }

    public static StoreCommentDto convertToDto(CommentModel comment) {
        return new StoreCommentDto(comment.getRate(), comment.getDescription(), comment.getCreated(),
                                   comment.getStore().getId(), comment.getHashCode());
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

    public float getRate() {
        return rate;
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
