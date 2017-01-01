package ir.asparsa.hobbytaste.net;

import ir.asparsa.common.net.dto.PageDto;
import ir.asparsa.common.net.dto.ResponseDto;
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

    @PUT(StoreServicePath.SERVICE + StoreServicePath.VIEWED)
    Observable<Collection<StoreDto>> storeViewed(@Path("storeId") Long id);

    @POST(StoreServicePath.SERVICE + StoreServicePath.COMMENTS)
    Observable<PageDto<StoreCommentDto>> loadComments(
            @Path("storeId") Long id,
            @Query("page") int page,
            @Query("size") int size
    );

    @PUT(StoreServicePath.SERVICE + StoreServicePath.COMMENTS)
    Observable<ResponseDto> saveComment(
            @Path("storeId") Long id,
            @Body StoreCommentDto comment
    );

    @PUT(StoreServicePath.SERVICE + StoreServicePath.LIKE)
    Observable<ResponseDto> like(
            @Path("storeId") Long id
    );

    @PUT(StoreServicePath.SERVICE + StoreServicePath.UNLIKE)
    Observable<ResponseDto> unlike(
            @Path("storeId") Long id
    );

    @PUT(StoreServicePath.SERVICE + StoreServicePath.LIKE_COMMENT)
    Observable<ResponseDto> likeComment(
            @Path("storeId") Long id,
            @Path("commentHashCode") Long commentHashCode
    );

    @PUT(StoreServicePath.SERVICE + StoreServicePath.UNLIKE_COMMENT)
    Observable<ResponseDto> unlikeComment(
            @Path("storeId") Long id,
            @Path("commentHashCode") Long commentHashCode
    );
}