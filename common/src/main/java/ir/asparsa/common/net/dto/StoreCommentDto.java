package ir.asparsa.common.net.dto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreCommentDto {

    private Float rate;
    private String description;
    private Long created;
    private Long storeId;
    private Long hashCode;

    public StoreCommentDto() {
    }

    public StoreCommentDto(
            float rate,
            String description,
            long created,
            long storeId,
            long hashCode
    ) {
        this.rate = rate;
        this.description = description;
        this.created = created;
        this.storeId = storeId;
        this.hashCode = hashCode;
    }

    public StoreCommentDto(
            String description,
            Long hashCode
    ) {
        this.description = description;
        this.hashCode = hashCode;
    }

    public String getDescription() {
        return description;
    }

    public float getRate() {
        return rate;
    }

    public long getCreated() {
        return created;
    }

    public long getStoreId() {
        return storeId;
    }

    public Long getHashCode() {
        return hashCode;
    }
}
