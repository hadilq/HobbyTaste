package ir.asparsa.common.net.dto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class BannerDto {
    private String mainUrl;
    private String thumbnailUrl;

    public BannerDto(String mainUrl, String thumbnailUrl) {
        this.mainUrl = mainUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
