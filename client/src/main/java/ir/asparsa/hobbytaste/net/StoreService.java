package ir.asparsa.hobbytaste.net;

import ir.asparsa.common.net.dto.PageDto;
import ir.asparsa.common.net.dto.StoreCommentDto;
import ir.asparsa.common.net.dto.StoreDto;
import ir.asparsa.common.net.path.StoreServicePath;
import retrofit2.http.*;
import rx.Observable;

import java.util.Collection;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
public interface StoreService {
    @GET(StoreServicePath.SERVICE)
    Observable<Collection<StoreDto>> loadStoreModels();

    @PUT(StoreServicePath.SERVICE)
    Observable<StoreDto> saveStore(@Body StoreDto store);

    @PUT(StoreServicePath.SERVICE + StoreServicePath.VIEWED)
    Observable<StoreDto> storeViewed(@Path("storeHashCode") Long storeHashCode);

    @POST(StoreServicePath.SERVICE + StoreServicePath.COMMENTS)
    Observable<PageDto<StoreCommentDto>> loadComments(
            @Path("storeHashCode") Long storeHashCode,
            @Query("page") int page,
            @Query("size") int size
    );

    @PUT(StoreServicePath.SERVICE + StoreServicePath.COMMENTS)
    Observable<StoreCommentDto> saveComment(
            @Path("storeHashCode") Long storeHashCode,
            @Body StoreCommentDto comment
    );

    @PUT(StoreServicePath.SERVICE + StoreServicePath.LIKE)
    Observable<StoreDto> like(
            @Path("storeHashCode") Long storeHashCode,
            @Path("like") Boolean like
    );

    @PUT(StoreServicePath.SERVICE + StoreServicePath.LIKE_COMMENT)
    Observable<StoreCommentDto> likeComment(
            @Path("storeHashCode") Long storeHashCode,
            @Path("commentHashCode") Long commentHashCode,
            @Path("like") Boolean like
    );
}
