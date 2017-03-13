package ir.asparsa.hobbytaste.net;

import ir.asparsa.common.net.dto.AuthenticateDto;
import ir.asparsa.common.net.path.UserServicePath;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public interface UserService {
    @POST(UserServicePath.SERVICE + UserServicePath.AUTHENTICATE)
    Observable<AuthenticateDto> authenticate(@Path("hashCode") long hashCode);

    @POST(UserServicePath.SERVICE + UserServicePath.USERNAME)
    Observable<AuthenticateDto> changeUsername(
            @Query("new") String username,
            @Path("hashCode") long hashCode
    );
}
