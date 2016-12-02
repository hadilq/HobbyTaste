package ir.asparsa.hobbytaste.net;

import retrofit2.http.POST;
import rx.Observable;

import java.util.Map;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public interface AuthenticateService {
    @POST("authenticate")
    Observable<Map<String, String>> authenticate();
}
