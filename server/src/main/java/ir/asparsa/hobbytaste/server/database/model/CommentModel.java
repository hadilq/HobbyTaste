package ir.asparsa.hobbytaste.server.database.model;

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

    public interface Columns {
        String ID = "id";
        String DESCRIPTION = "description";
        String RATE = "rate";
        String STORE = "store";
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = Columns.DESCRIPTION)
    private String description;
    @Column(name = Columns.RATE)
    private float rate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Columns.STORE)
    private StoreModel store;

    public CommentModel() {
    }

    public CommentModel(
            String description,
            float rate,
            StoreModel store
    ) {
        this.description = description;
        this.rate = rate;
        this.store = store;
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
