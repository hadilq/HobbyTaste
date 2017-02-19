package ir.asparsa.common.net.dto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreCommentDto {

    private String description;
    private String creator;
    private Long created;
    private Long storeHashCode;
    private Long hashCode;
    private Long rate;
    private Boolean like;

    public StoreCommentDto() {
    }

    public StoreCommentDto(
            String description,
            String creator,
            Long created,
            Long storeHashCode,
            Long hashCode,
            Long rate,
            Boolean like
    ) {
        this.description = description;
        this.creator = creator;
        this.created = created;
        this.storeHashCode = storeHashCode;
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

    public String getCreator() {
        return creator;
    }

    public long getCreated() {
        return created;
    }

    public long getStoreHashCode() {
        return storeHashCode;
    }

    public long getHashCode() {
        return hashCode;
    }

    public Boolean getLike() {
        return like;
    }
}
