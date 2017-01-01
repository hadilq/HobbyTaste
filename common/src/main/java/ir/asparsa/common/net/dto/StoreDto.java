package ir.asparsa.common.net.dto;

import java.util.List;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreDto {

    private Long id;

    private Double lat;
    private Double lon;

    private String title;
    private Long viewed;
    private Long rate;
    private Boolean like;
    private String description;
    private List<BannerDto> banners;

    public StoreDto(
            Long id,
            Double lat,
            Double lon,
            String title,
            Long viewed,
            Long rate,
            Boolean like,
            String description,
            List<BannerDto> banners
    ) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.title = title;
        this.viewed = viewed;
        this.rate = rate;
        this.like = like;
        this.description = description;
        this.banners = banners;
    }

    public Long getId() {
        return id;
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

    public List<BannerDto> getBanners() {
        return banners;
    }

    public String getDescription() {
        return description;
    }
}
