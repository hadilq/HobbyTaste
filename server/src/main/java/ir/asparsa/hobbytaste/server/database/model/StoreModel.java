package ir.asparsa.hobbytaste.server.database.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;

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
        String BANNER_URL = "banner";
        String ICON_URL = "icon";
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
    private float rate;
    @Column(name = Columns.BANNER_URL)
    private String bannerUrl;
    @Column(name = Columns.ICON_URL)
    private String iconUrl;

    StoreModel() {
    }

    public StoreModel(Double lat, Double lon, String title) {
        this.lon = lon;
        this.lat = lat;
        this.title = title;
    }

    public StoreModel(
            Double lat, Double lon, String title, String description, float rate, String bannerUrl,
            String iconUrl) {
        this.lat = lat;
        this.lon = lon;
        this.title = title;
        this.description = description;
        this.rate = rate;
        this.bannerUrl = bannerUrl;
        this.iconUrl = iconUrl;
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

    public float getRate() {
        return rate;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public String getIconUrl() {
        return iconUrl;
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
               ", bannerUrl='" + bannerUrl + '\'' +
               ", iconUrl='" + iconUrl + '\'' +
               '}';
    }
}
