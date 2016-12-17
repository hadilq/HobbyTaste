package ir.asparsa.hobbytaste.server.database.model;

import ir.asparsa.common.net.dto.BannerDto;
import ir.asparsa.common.net.dto.StoreDetailsDto;
import ir.asparsa.common.net.dto.StoreLightDto;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    public interface Columns {
        String ID = "id";
        String LAT = "lat";
        String LON = "lon";
        String TITLE = "title";
        String DESCRIPTION = "description";
        String RATE = "rate";
        String BANNERS = "banners";
        String COMMENTS = "comments";
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = Columns.LAT)
    private Double lat;
    @Column(name = Columns.LON)
    private Double lon;

    @Column(name = Columns.TITLE)
    private String title;
    @Column(name = Columns.DESCRIPTION)
    private String description;
    @Column(name = Columns.RATE)
    private Float rate;

    @OneToMany(mappedBy = "store", fetch = FetchType.EAGER)
    @Column(name = Columns.BANNERS)
    private Collection<BannerModel> banners;

    StoreModel() {
    }

    public StoreModel(
            Double lat,
            Double lon,
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

    public StoreLightDto lightConvert() {
        List<BannerDto> bannerDtos = new ArrayList<>();
        if (banners != null && banners.size() != 0) {
            for (BannerModel banner : banners) {
                bannerDtos.add(new BannerDto(banner.getMainUrl(), banner.getThumbnailUrl()));
            }
        }
        return new StoreLightDto(id, lat, lon, title, rate, bannerDtos);
    }

    public StoreDetailsDto detailsConvert() {
        return new StoreDetailsDto(description);
    }

    public Long getId() {
        return id;
    }

    public Double getLon() {
        return lon;
    }

    public Double getLat() {
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
        return new EqualsBuilder()
                .append(getLat(), other.getLat())
                .append(getLon(), other.getLon())
                .append(getId(), other.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getLat()).append(getLon()).append(getId()).toHashCode();
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
