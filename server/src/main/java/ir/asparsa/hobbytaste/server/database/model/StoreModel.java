package ir.asparsa.hobbytaste.server.database.model;

import ir.asparsa.common.database.model.StoreColumns;
import ir.asparsa.common.net.dto.BannerDto;
import ir.asparsa.common.net.dto.StoreDto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@Entity
@Table(name = "stores")
public class StoreModel implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = StoreColumns.LAT)
    private double lat;
    @Column(name = StoreColumns.LON)
    private double lon;

    @Column(name = StoreColumns.TITLE)
    private String title;
    @Column(name = StoreColumns.DESCRIPTION)
    private String description;
    @Column(name = StoreColumns.RATE)
    private Float rate;

    @OneToMany(mappedBy = "store", fetch = FetchType.EAGER)
    @Column(name = StoreColumns.BANNERS)
    private Collection<BannerModel> banners;

    StoreModel() {
    }

    public StoreModel(
            double lat,
            double lon,
            String title,
            String description,
            Float rate
    ) {
        this.lat = lat;
        this.lon = lon;
        this.title = title;
        this.description = description;
        this.rate = rate;
    }

    public StoreDto convertToDto() {
        List<BannerDto> banners = new ArrayList<>();
        if (this.banners != null && this.banners.size() != 0) {
            for (BannerModel banner : this.banners) {
                banners.add(new BannerDto(banner.getMainUrl(), banner.getThumbnailUrl()));
            }
        }
        return new StoreDto(id, lat, lon, title, rate, description, banners);
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

    public Float getRate() {
        return rate;
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
        return (getLat() == other.getLat() &&
                getLon() == other.getLon() &&
                getId() == other.getId());
    }

    @Override
    public int hashCode() {
        long lat = Double.doubleToLongBits(getLat());
        long lon = Double.doubleToLongBits(getLon());
        return (int) (((lat ^ lat >> 31) ^ (lon ^ lon >> 31)) ^ (getId() ^ getId() >> 31));
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
