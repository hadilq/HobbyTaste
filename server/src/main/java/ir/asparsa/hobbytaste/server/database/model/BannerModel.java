package ir.asparsa.hobbytaste.server.database.model;

import ir.asparsa.common.database.model.Banner;

import javax.persistence.*;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
@Entity
@Table(name = Banner.TABLE_NAME)
public class BannerModel {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = Banner.Columns.MAIN_NAME)
    private String mainName;
    @Column(name = Banner.Columns.THUMBNAIL_NAME)
    private String thumbnailName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Banner.Columns.STORE)
    private StoreModel store;

    public BannerModel() {
    }

    public BannerModel(
            String mainName,
            String thumbnailName,
            StoreModel store
    ) {
        this.mainName = mainName;
        this.thumbnailName = thumbnailName;
        this.store = store;
    }

    public long getId() {
        return id;
    }

    public String getMainName() {
        return mainName;
    }

    public String getThumbnailName() {
        return thumbnailName;
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
        return ((getMainName() == null && other.getMainName() == null) ||
                (getMainName() != null && getMainName().equals(other.getMainName()))) &&
               ((getThumbnailName() == null && other.getThumbnailName() == null) ||
                (getThumbnailName() != null && getThumbnailName().equals(other.getThumbnailName()))) &&
               ((getStore() == null && other.getStore() == null) ||
                (getStore() != null && getStore().equals(other.getStore())));
    }

    @Override
    public int hashCode() {
        return ((getMainName() == null ? 0 : getMainName().hashCode()) ^
                (getThumbnailName() == null ? 0 : getThumbnailName().hashCode())) ^ (int) ((getId() ^ getId() >> 31));
    }

    @Override public String toString() {
        return "BannerModel{" +
               "id=" + id +
               ", mainName='" + mainName + '\'' +
               ", thumbnailName='" + thumbnailName + '\'' +
               ", storeId=" + store.getId() +
               '}';
    }

}
