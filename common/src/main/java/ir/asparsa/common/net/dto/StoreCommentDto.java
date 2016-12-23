package ir.asparsa.common.net.dto;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class StoreCommentDto {

    private Long id;
    private Float rate;
    private String description;
    private Long created;
    private Long storeId;
    private Integer hashCode;

    public StoreCommentDto(
            long id,
            float rate,
            String description,
            long created,
            long storeId
    ) {
        this.id = id;
        this.rate = rate;
        this.description = description;
        this.created = created;
        this.storeId = storeId;
    }

    public StoreCommentDto(
            String description,
            Integer hashCode
    ) {
        this.description = description;
        this.hashCode = hashCode;
    }

    public long getId() {
        return id;
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

    public Integer getHashCode() {
        return hashCode;
    }
}
