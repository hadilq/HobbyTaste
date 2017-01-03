package ir.asparsa.common.net.dto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreCommentDto {

    private String description;
    private Long created;
    private Long storeId;
    private Long hashCode;
    private Long rate;
    private Boolean like;

    public StoreCommentDto() {
    }

    public StoreCommentDto(
            String description,
            Long created,
            Long storeId,
            Long hashCode,
            Long rate,
            Boolean like
    ) {
        this.description = description;
        this.created = created;
        this.storeId = storeId;
        this.hashCode = hashCode;
        this.rate = rate;
        this.like = like;
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

    public long getRate() {
        return rate;
    }

    public long getCreated() {
        return created;
    }

    public long getStoreId() {
        return storeId;
    }

    public long getHashCode() {
        return hashCode;
    }

    public Boolean getLike() {
        return like;
    }
}
