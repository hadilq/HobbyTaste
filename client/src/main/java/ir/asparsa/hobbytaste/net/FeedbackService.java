package ir.asparsa.hobbytaste.net;

import ir.asparsa.common.net.dto.FeedbackProto;
import ir.asparsa.common.net.dto.ResponseProto;
import ir.asparsa.common.net.path.FeedbackServicePath;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * @author hadi
 * @since 3/18/2017 AD.
 */
public interface FeedbackService {

    @POST(FeedbackServicePath.SERVICE)
    Observable<ResponseProto.Response> feedback(
            @Body FeedbackProto.Feedback feedbackDto
    );
}
