package ir.asparsa.common.net.path;

/**
 * Created by hadi on 12/31/2016 AD.
 */
public interface StoreServicePath {
    String SERVICE = "store";
    String VIEWED = "/{storeId}/viewed";
    String COMMENTS = "/{storeId}/comments";
    String LIKE = "/{storeId}/liked/{like}";
    String LIKE_COMMENT = "/{storeId}/comment/{commentHashCode}/like/{like}";
}
