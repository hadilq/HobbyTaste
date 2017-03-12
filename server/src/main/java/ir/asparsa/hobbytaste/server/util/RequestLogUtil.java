package ir.asparsa.hobbytaste.server.util;

import ir.asparsa.hobbytaste.server.database.model.AccountModel;
import ir.asparsa.hobbytaste.server.database.model.RequestLogModel;
import ir.asparsa.hobbytaste.server.database.repository.RequestLogRepository;
import ir.asparsa.hobbytaste.server.exception.DdosSecurityException;
import ir.asparsa.hobbytaste.server.resources.Strings;
import ir.asparsa.hobbytaste.server.security.model.JwtAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author hadi
 * @since 3/12/2017 AD.
 */
@Component
public class RequestLogUtil {

    @Value("${security.request.minInterval}")
    private int minInterval;

    @Autowired
    RequestLogRepository requestLogRepository;

    public void asyncLog(
            HttpServletRequest request,
            AccountModel account
    ) {
        asyncLog(getModel(request, account));
    }

    public void asyncLog(
            JwtAuthenticationToken jwtAuthenticationToken,
            AccountModel account
    ) {
        asyncLog(getModel(jwtAuthenticationToken, account));
    }

    private void asyncLog(RequestLogModel model) {
        List<RequestLogModel> previousRequests = requestLogRepository
                .findTop10ByAddressOrderByDatetimeAsc(model.getAddress());
        if (previousRequests.size() == 10) {
            long averageTime = 0L;
            for (RequestLogModel previousRequest : previousRequests) {
                averageTime += previousRequest.getDatetime();
            }
            averageTime /= 10;
            if (model.getDatetime() - averageTime < minInterval) {
                throw new DdosSecurityException(
                        "JWT token is not valid", Strings.SECURITY_DDOS, Strings.DEFAULT_LOCALE);
            }
        }

        Observable.create((Observable.OnSubscribe<Void>) subscriber -> requestLogRepository.save(model)
        )
                  .subscribeOn(Schedulers.newThread())
                  .subscribe(new Observer<Void>() {
                      @Override public void onCompleted() {
                      }

                      @Override public void onError(Throwable e) {
                      }

                      @Override public void onNext(Void aVoid) {
                      }
                  });
    }

    private RequestLogModel getModel(
            HttpServletRequest request,
            AccountModel account
    ) {
        return new RequestLogModel(
                System.currentTimeMillis(), request.getRemoteAddr(), request.getRequestURI(), account);
    }

    private RequestLogModel getModel(
            JwtAuthenticationToken jwtAuthenticationToken,
            AccountModel account
    ) {
        return new RequestLogModel(
                System.currentTimeMillis(), jwtAuthenticationToken.getAddress(), jwtAuthenticationToken.getUri(),
                account);
    }
}
