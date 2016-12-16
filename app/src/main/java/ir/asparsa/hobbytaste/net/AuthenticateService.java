package ir.asparsa.hobbytaste.net;

import ir.asparsa.common.net.dto.AuthenticateDto;
import retrofit2.http.POST;
import rx.Observable;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public interface AuthenticateService {
    @POST("user/authenticate")
    Observable<AuthenticateDto> authenticate();
}
