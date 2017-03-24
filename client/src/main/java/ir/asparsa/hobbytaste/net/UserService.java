package ir.asparsa.hobbytaste.net;

import ir.asparsa.common.net.dto.AuthenticateProto;
import ir.asparsa.common.net.path.UserServicePath;
import retrofit2.http.Body;
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
    Observable<AuthenticateProto.Authenticate> authenticate(
            @Body AuthenticateProto.Request oldTokenDto
    );

    @POST(UserServicePath.SERVICE + UserServicePath.USERNAME)
    Observable<AuthenticateProto.Authenticate> changeUsername(
            @Path("hashCode") long hashCode,
            @Query("new") String username
    );
}
