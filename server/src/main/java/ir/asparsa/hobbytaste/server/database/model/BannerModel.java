package ir.asparsa.hobbytaste.server.database.model;

import ir.asparsa.common.database.model.BannerColumns;

import javax.persistence.*;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
@Entity
@Table(name = "banners")
public class BannerModel {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = BannerColumns.MAIN_URL)
    private String mainUrl;
    @Column(name = BannerColumns.THUMBNAIL_URL)
    private String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = BannerColumns.STORE)
    private StoreModel store;

    public BannerModel() {
    }

    public BannerModel(
            String mainUrl,
            String thumbnailUrl,
            StoreModel store
    ) {
        this.mainUrl = mainUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.store = store;
    }

    public long getId() {
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
        return ((getMainUrl() == null && other.getMainUrl() == null) ||
                (getMainUrl() != null && getMainUrl().equals(other.getMainUrl()))) &&
               ((getThumbnailUrl() == null && other.getThumbnailUrl() == null) ||
                (getThumbnailUrl() != null && getThumbnailUrl().equals(other.getThumbnailUrl()))) &&
               ((getId() == other.getId()));
    }

    @Override
    public int hashCode() {
        return ((getMainUrl() == null ? 0 : getMainUrl().hashCode()) ^
                (getThumbnailUrl() == null ? 0 : getThumbnailUrl().hashCode())) ^ (int) ((getId() ^ getId() >> 31));
    }

    @Override public String toString() {
        return "BannerModel{" +
               "id=" + id +
               ", mainUrl='" + mainUrl + '\'' +
               ", thumbnailUrl='" + thumbnailUrl + '\'' +
               ", storeId=" + store.getId() +
               '}';
    }
}
