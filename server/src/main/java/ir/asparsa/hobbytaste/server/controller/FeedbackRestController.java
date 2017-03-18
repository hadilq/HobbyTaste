package ir.asparsa.hobbytaste.server.controller;

import ir.asparsa.common.net.dto.FeedbackDto;
import ir.asparsa.common.net.dto.ResponseDto;
import ir.asparsa.common.net.path.FeedbackServicePath;
import ir.asparsa.hobbytaste.server.database.model.FeedbackModel;
import ir.asparsa.hobbytaste.server.database.repository.FeedbackRepository;
import ir.asparsa.hobbytaste.server.security.config.WebSecurityConfig;
import ir.asparsa.hobbytaste.server.security.model.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * @author hadi
 * @since 1/17/2017 AD.
 */
@RestController
@RequestMapping(WebSecurityConfig.ENTRY_POINT_API + "/" + FeedbackServicePath.SERVICE) class FeedbackRestController {

    private final static Logger logger = LoggerFactory.getLogger(FeedbackRestController.class);

    @Autowired
    FeedbackRepository feedbackRepository;

    public FeedbackRestController() {
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    ResponseDto feedback(
            @RequestBody FeedbackDto feedbackDto,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        logger.info("The feedback is: " + feedbackDto.getBody());
        Observable.create((Observable.OnSubscribe<Void>) subscriber -> feedbackRepository
                .save(new FeedbackModel(
                        feedbackDto.getBody(), feedbackDto.getCrashThrowableName(), feedbackDto.getCrashMessage(),
                        user.getAccount())))
                  .subscribeOn(Schedulers.newThread())
                  .subscribe(new Observer<Void>() {
                      @Override public void onCompleted() {
                      }

                      @Override public void onError(Throwable e) {
                      }

                      @Override public void onNext(Void aVoid) {
                      }
                  });
        return new ResponseDto(ResponseDto.STATUS.SUCCEED);
    }
}
