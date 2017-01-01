package ir.asparsa.common.net.path;

/**
 * Created by hadi on 12/31/2016 AD.
 */
public interface StoreServicePath {
    String SERVICE = "store";
    String VIEWED = "/{storeId}/viewed";
    String COMMENTS = "/{storeId}/comments";
    String LIKE = "/{storeId}/liked";
    String UNLIKE = "/{storeId}/unlike";
    String LIKE_COMMENT = "/{storeId}/comment/{commentHashCode}/like";
    String UNLIKE_COMMENT = "/{storeId}/comment/{commentHashCode}/unlike";
}
