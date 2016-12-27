package ir.asparsa.hobbytaste.net;

import ir.asparsa.common.net.dto.PageDto;
import ir.asparsa.common.net.dto.ResponseDto;
import ir.asparsa.common.net.dto.StoreCommentDto;
import ir.asparsa.common.net.dto.StoreDto;
import retrofit2.http.*;
import rx.Observable;

import java.util.Collection;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
public interface StoreService {
    @GET("stores")
    Observable<Collection<StoreDto>> loadStoreModels();

    @POST("stores/{storeId}/comments")
    Observable<PageDto<StoreCommentDto>> loadComments(
            @Path("storeId") Long id,
            @Query("page") int page,
            @Query("size") int size
    );

    @PUT("stores/{storeId}/comments")
    Observable<ResponseDto> saveComment(
            @Path("storeId") Long id,
            @Body StoreCommentDto comment
    );
}
