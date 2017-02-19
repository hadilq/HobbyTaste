package ir.asparsa.common.net.dto;

import java.util.List;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreDto {

    private Double lat;
    private Double lon;

    private String title;
    private Long viewed;
    private Long rate;
    private Boolean like;
    private String description;
    private Long hashCode;
    private Long created;
    private List<BannerDto> banners;

    public StoreDto() {
    }

    public StoreDto(
            Double lat,
            Double lon,
            String title,
            String description,
            Long hashCode,
            List<BannerDto> banners
    ) {
        this.lat = lat;
        this.lon = lon;
        this.title = title;
        this.description = description;
        this.hashCode = hashCode;
        this.banners = banners;
    }

    public StoreDto(
            Double lat,
            Double lon,
            String title,
            Long viewed,
            Long rate,
            Boolean like,
            String description,
            Long hashCode,
            Long created,
            List<BannerDto> banners
    ) {
        this.lat = lat;
        this.lon = lon;
        this.title = title;
        this.viewed = viewed;
        this.rate = rate;
        this.like = like;
        this.description = description;
        this.hashCode = hashCode;
        this.created = created;
        this.banners = banners;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getTitle() {
        return title;
    }

    public Long getViewed() {
        return viewed;
    }

    public Long getRate() {
        return rate;
    }

    public Boolean getLike() {
        return like;
    }

    public Long getHashCode() {
        return hashCode;
    }

    public Long getCreated() {
        return created;
    }

    public List<BannerDto> getBanners() {
        return banners;
    }

    public String getDescription() {
        return description;
    }
}
