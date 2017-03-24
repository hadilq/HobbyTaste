package ir.asparsa.hobbytaste.net;

import ir.asparsa.common.net.dto.CommentProto;
import ir.asparsa.common.net.dto.StoreProto;
import ir.asparsa.common.net.path.StoreServicePath;
import retrofit2.http.*;
import rx.Observable;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
public interface StoreService {
    @GET(StoreServicePath.SERVICE)
    Observable<StoreProto.Stores> loadStoreModels();

    @PUT(StoreServicePath.SERVICE)
    Observable<StoreProto.Store> saveStore(@Body StoreProto.Store store);

    @PUT(StoreServicePath.SERVICE + StoreServicePath.VIEWED)
    Observable<StoreProto.Store> storeViewed(@Path("storeHashCode") Long storeHashCode);

    @POST(StoreServicePath.SERVICE + StoreServicePath.COMMENTS)
    Observable<CommentProto.Comments> loadComments(
            @Path("storeHashCode") Long storeHashCode,
            @Query("page") int page,
            @Query("size") int size
    );

    @PUT(StoreServicePath.SERVICE + StoreServicePath.COMMENTS)
    Observable<CommentProto.Comment> saveComment(
            @Path("storeHashCode") Long storeHashCode,
            @Body CommentProto.Comment comment
    );

    @PUT(StoreServicePath.SERVICE + StoreServicePath.LIKE)
    Observable<StoreProto.Store> like(
            @Path("storeHashCode") Long storeHashCode,
            @Path("like") Boolean like
    );

    @PUT(StoreServicePath.SERVICE + StoreServicePath.LIKE_COMMENT)
    Observable<CommentProto.Comment> likeComment(
            @Path("storeHashCode") Long storeHashCode,
            @Path("commentHashCode") Long commentHashCode,
            @Path("like") Boolean like
    );
}
