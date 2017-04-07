package ir.asparsa.common.net.path;

/**
 * @author hadi
 * @since 12/31/2016 AD.
 */
public interface StoreServicePath {
    String SERVICE = "store";
    String VIEWED = "/{storeHashCode}/viewed";
    String COMMENTS = "/{storeHashCode}/comments";
    String LIKE = "/{storeHashCode}/liked/{like}";
    String LIKE_COMMENT = "/{storeHashCode}/comment/{commentHashCode}/like/{like}";
}
