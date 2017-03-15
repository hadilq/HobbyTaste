package ir.asparsa.hobbytaste.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import ir.asparsa.hobbytaste.BuildConfig;
import ir.asparsa.hobbytaste.R;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class SplashActivity extends BaseActivity {

    private static final long SPLASH_FINISH_DELAY_TIME = BuildConfig.DEBUG ? 1 : 5;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
    }

    @Override protected void onResume() {
        super.onResume();
        Observable
                .timer(SPLASH_FINISH_DELAY_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(PublishSubject.create())
                .subscribe(new Action1<Long>() {
                    @Override public void call(Long aLong) {
                        finish();
                    }
                });
    }

    @Override public void onBackPressed() {
    }
}
