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

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "lon")
    private Double lon;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "title")
    private String title;

    StoreModel() {
    }

    public StoreModel(Double lat, Double lon, String title) {
        this.lon = lon;
        this.lat = lat;
        this.title = title;
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
               ", lon=" + lon +
               ", lat=" + lat +
               ", title='" + title + '\'' +
               '}';
    }
}
