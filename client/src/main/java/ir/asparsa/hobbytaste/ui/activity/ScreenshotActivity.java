package ir.asparsa.hobbytaste.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import ir.asparsa.hobbytaste.R;

/**
 * Created by hadi on 1/13/2017 AD.
 */
public class ScreenshotActivity extends BaseActivity {

    public static final String BUNDLE_KEY_MAIN_URL = "BUNDLE_KEY_MAIN_URL";
    public static final String BUNDLE_KEY_THUMBNAIL_URL = "BUNDLE_KEY_THUMBNAIL_URL";

    @BindView(R.id.image)
    ImageView mScreenshotImageView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.close_button)
    View mCloseButton;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screenshot_full);

        ButterKnife.bind(this);

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                ScreenshotActivity.this.finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        String mainUrl = extras.getString(BUNDLE_KEY_MAIN_URL);
        String thumbnailUrl = extras.getString(BUNDLE_KEY_THUMBNAIL_URL);

        RequestCreator requestCreator = null;
        if (!TextUtils.isEmpty(mainUrl)) {
            requestCreator = Picasso.with(this)
                                    .load(mainUrl);
        } else if (!TextUtils.isEmpty(thumbnailUrl)) {
            requestCreator = Picasso.with(this)
                                    .load(thumbnailUrl);
        }

        if (requestCreator != null) {
            requestCreator.into(mScreenshotImageView, new Callback() {
                @Override public void onSuccess() {
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override public void onError() {
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }

    }
}
