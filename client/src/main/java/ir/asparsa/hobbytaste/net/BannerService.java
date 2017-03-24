package ir.asparsa.hobbytaste.net;

import ir.asparsa.common.net.dto.StoreProto;
import ir.asparsa.common.net.path.BannerServicePath;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * @author hadi
 * @since 1/17/2017 AD.
 */
public interface BannerService {

    @Multipart
    @POST(BannerServicePath.SERVICE)
    Observable<StoreProto.Banner> handleFileUpload(
            @Part MultipartBody.Part file
    );
}
