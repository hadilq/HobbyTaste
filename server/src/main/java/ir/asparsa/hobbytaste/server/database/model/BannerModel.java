package ir.asparsa.hobbytaste.server.database.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
@Entity
@Table(name = "banners")
public class BannerModel {

    public interface Columns {
        String ID = "id";
        String MAIN_URL = "main_url";
        String THUMBNAIL_URL = "thumbnail_url";
        String STORE = "store";
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = Columns.MAIN_URL)
    private String mainUrl;
    @Column(name = Columns.THUMBNAIL_URL)
    private String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Columns.STORE)
    private StoreModel store;

    public BannerModel() {
    }

    public BannerModel(String mainUrl, String thumbnailUrl, StoreModel store) {
        this.mainUrl = mainUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.store = store;
    }

    public Long getId() {
        return id;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public StoreModel getStore() {
        return store;
    }

    @Override
    public boolean equals(final Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof BannerModel)) {
            return false;
        }
        final BannerModel other = (BannerModel) otherObj;
        return new EqualsBuilder()
                .append(getMainUrl(), other.getMainUrl())
                .append(getThumbnailUrl(), other.getThumbnailUrl())
                .append(getId(), other.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getMainUrl()).append(getThumbnailUrl()).append(getId()).toHashCode();
    }

    @Override public String toString() {
        return "BannerModel{" +
               "id=" + id +
               ", mainUrl='" + mainUrl + '\'' +
               ", thumbnailUrl='" + thumbnailUrl + '\'' +
               ", store=" + store +
               '}';
    }
}
