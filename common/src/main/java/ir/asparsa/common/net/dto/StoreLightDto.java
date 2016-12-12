package ir.asparsa.common.net.dto;

import java.util.List;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreLightDto {

    private Long id;

    private Double lat;
    private Double lon;

    private String title;
    private float rate;
    private List<BannerDto> banners;

    public StoreLightDto(
            Long id, Double lat, Double lon, String title, float rate, List<BannerDto> banners) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.title = title;
        this.rate = rate;
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

    public float getRate() {
        return rate;
    }

    public List<BannerDto> getBanners() {
        return banners;
    }
}
