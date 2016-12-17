package ir.asparsa.common.net.dto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreCommentDto {

    private float rate;
    private String description;

    public StoreCommentDto(
            float rate,
            String description
    ) {
        this.rate = rate;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public float getRate() {
        return rate;
    }
}
