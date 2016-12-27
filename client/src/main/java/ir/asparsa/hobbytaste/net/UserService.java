package ir.asparsa.hobbytaste.net;

import ir.asparsa.common.net.dto.AuthenticateDto;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public interface UserService {
    @POST("user/authenticate")
    Observable<AuthenticateDto> authenticate();

    @POST("user/username")
    Observable<AuthenticateDto> changeUsername(@Query("new") String username);
}
