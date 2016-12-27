package ir.asparsa.hobbytaste.server.database.model;

import ir.asparsa.common.database.model.CommentColumns;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    private Long id;

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

    public Long getId() {
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
        return new EqualsBuilder()
                .append(getId(), other.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

    @Override public String toString() {
        return "CommentModel{" +
               "id=" + id +
               ", description='" + description + '\'' +
               ", rate='" + rate + '\'' +
               ", store=" + store +
               '}';
    }
}
