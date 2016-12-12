package ir.asparsa.hobbytaste.net;

import ir.asparsa.common.net.dto.StoreDetailsDto;
import ir.asparsa.common.net.dto.StoreLightDto;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

import java.util.Collection;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
public interface StoreService {
    @GET("stores")
    Observable<Collection<StoreLightDto>> loadStoreLightModels();

    @GET("store/details")
    Observable<StoreDetailsDto> loadStoreDetailsModels(@Query("id") Long id);
}
