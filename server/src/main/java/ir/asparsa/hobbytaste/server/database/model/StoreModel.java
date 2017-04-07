package ir.asparsa.hobbytaste.server.database.model;

import ir.asparsa.common.database.model.Store;
import ir.asparsa.common.net.dto.StoreProto;
import ir.asparsa.hobbytaste.server.storage.StorageService;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@Entity
@Table(name = Store.TABLE_NAME)
public class StoreModel implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = Store.Columns.LAT)
    private double lat;
    @Column(name = Store.Columns.LON)
    private double lon;

    @Column(name = Store.Columns.TITLE)
    private String title;
    @Column(name = Store.Columns.DESCRIPTION)
    private String description;
    @Column(name = Store.Columns.HASH_CODE)
    private long hashCode;
    @Column(name = Store.Columns.CREATED)
    private long created;
    @Column(name = Store.Columns.VIEWED)
    private long viewed;
    @Column(name = Store.Columns.RATE)
    private long rate;


    @OneToMany(mappedBy = "store", fetch = FetchType.EAGER)
    @Column(name = Store.Columns.BANNERS)
    private Collection<BannerModel> banners;

    StoreModel() {
    }

    public static StoreModel newInstance(StoreProto.Store store) {
        StoreModel storeModel = new StoreModel();
        storeModel.lat = store.getLat();
        storeModel.lon = store.getLon();
        storeModel.title = store.getTitle();
        storeModel.description = store.getDescription();
        storeModel.hashCode = store.getHashCode();
        storeModel.created = System.currentTimeMillis();
        return storeModel;
    }

    public StoreProto.Store convertToDto(
            boolean like,
            StorageService storageService
    ) {
        StoreProto.Store.Builder builder = StoreProto.Store
                .newBuilder()
                .setLat(lat)
                .setLon(lon)
                .setTitle(title)
                .setViewed(viewed)
                .setRate(rate)
                .setLike(like)
                .setDescription(description)
                .setHashCode(hashCode)
                .setCreated(created);
        if (this.banners != null && this.banners.size() != 0) {
            for (BannerModel banner : this.banners) {
                builder.addBanner(StoreProto.Banner
                                          .newBuilder()
                                          .setMainUrl(storageService.getServerFileUrl(banner.getMainName()))
                                          .setThumbnailUrl(storageService.getServerFileUrl(banner.getThumbnailName()))
                                          .build());
            }
        }
        return builder.build();
    }

    public long getId() {
        return id;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getHashCode() {
        return hashCode;
    }

    public long getCreated() {
        return created;
    }

    public long getViewed() {
        return viewed;
    }

    public long increaseViewed() {
        return ++viewed;
    }

    public long getRate() {
        return rate;
    }

    public long increaseRate() {
        return ++rate;
    }

    public Long decreaseRate() {
        return --rate;
    }

    public Collection<BannerModel> getBanners() {
        return banners;
    }

    public void setBanners(Collection<BannerModel> banners) {
        this.banners = banners;
    }

    @Override
    public boolean equals(final Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof StoreModel)) {
            return false;
        }
        final StoreModel other = (StoreModel) otherObj;
        return getHashCode() == other.getHashCode();
    }

    @Override public String toString() {
        return "StoreModel{" +
               "id=" + id +
               ", lat=" + lat +
               ", lon=" + lon +
               ", title='" + title + '\'' +
               ", description='" + description + '\'' +
               ", rate=" + rate +
               ", banners=" + banners +
               '}';
    }
}
