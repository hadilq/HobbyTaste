package ir.asparsa.hobbytaste.ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import ir.asparsa.hobbytaste.R;

/**
 * Created by hadi on 1/13/2017 AD.
 */
public class ScreenshotActivity extends BaseActivity {

    public static final String BUNDLE_KEY_IMAGE = "BUNDLE_KEY_IMAGE";
    public static final String BUNDLE_KEY_MAIN_URL = "BUNDLE_KEY_MAIN_URL";
    public static final String BUNDLE_KEY_THUMBNAIL_URL = "BUNDLE_KEY_THUMBNAIL_URL";

    @BindView(R.id.image)
    ImageView mScreenshotImageView;
    @BindView(R.id.close_button)
    Button mCloseButton;

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
        Bitmap bitmap = extras.getParcelable(BUNDLE_KEY_IMAGE);
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

        if (bitmap != null && requestCreator != null) {
            requestCreator.placeholder(new BitmapDrawable(getResources(), bitmap));
        }

        if (requestCreator != null) {
            requestCreator.into(mScreenshotImageView);
        }

    }
}
