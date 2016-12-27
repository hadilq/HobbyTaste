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
    private Float rate;
    private String description;
    private List<BannerDto> banners;

    public StoreDto(
            Long id,
            Double lat,
            Double lon,
            String title,
            Float rate,
            String description,
            List<BannerDto> banners
    ) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.title = title;
        this.rate = rate;
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

    public Float getRate() {
        return rate;
    }

    public List<BannerDto> getBanners() {
        return banners;
    }

    public String getDescription() {
        return description;
    }
}
