package ir.asparsa.hobbytaste.net;

import ir.asparsa.hobbytaste.database.model.StoreModel;
import retrofit2.http.GET;
import rx.Observable;

import java.util.Collection;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
public interface StoreService {
    @GET("stores")
    Observable<Collection<StoreModel>> loadStoreModels();
}
