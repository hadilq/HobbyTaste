package ir.asparsa.hobbytaste.net;

import ir.asparsa.common.net.dto.PageDto;
import ir.asparsa.common.net.dto.StoreCommentDto;
import ir.asparsa.common.net.dto.StoreDetailsDto;
import ir.asparsa.common.net.dto.StoreLightDto;
import retrofit2.http.*;
import rx.Observable;

import java.util.Collection;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
public interface StoreService {
    @GET("stores")
    Observable<Collection<StoreLightDto>> loadStoreLightModels();

    @GET("stores/{storeId}/details")
    Observable<StoreDetailsDto> loadStoreDetailsModels(@Path("storeId") Long id);

    @POST("stores/{storeId}/comments")
    Observable<PageDto<StoreCommentDto>> loadComments(
            @Path("storeId") Long id,
            @Query("page") int page,
            @Query("size") int size
    );

    @PUT("stores/{storeId}/comments")
    Observable<PageDto<StoreCommentDto>> saveComments(
            @Path("storeId") Long id,
            @Body StoreCommentDto comment
    );
}
