package ir.asparsa.hobbytaste.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import ir.asparsa.hobbytaste.R;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class SplashActivity extends Activity {

    // TODO: increase it in release
    private static final long SPLASH_FINISH_DELAY_TIME = 1000;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
    }

    @Override protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                finish();
            }
        }, SPLASH_FINISH_DELAY_TIME);
    }
}
