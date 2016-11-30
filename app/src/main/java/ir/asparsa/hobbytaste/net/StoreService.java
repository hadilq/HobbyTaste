package ir.asparsa.hobbytaste.net;

import ir.asparsa.hobbytaste.database.model.StoreModel;
import retrofit2.http.GET;
import rx.Observable;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
public interface StoreService {
    @GET("list")
    Observable<StoreModel> loadStoreModels();
}
