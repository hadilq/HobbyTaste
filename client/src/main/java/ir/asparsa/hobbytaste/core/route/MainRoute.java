package ir.asparsa.hobbytaste.core.route;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.MainContentFragment;

/**
 * @author hadi
 * @since 4/25/2017 AD.
 */
public class MainRoute implements Route {

    public static final int PAGE = 0;

    private final String mSegment;

    MainRoute(Resources resources) {
        mSegment = resources.getString(R.string.path_segment_main);
    }

    @Override public boolean shouldFire(AnalysedUri uri) {
        return uri.getLowerCasePathSegments().size() > 0 && uri.getLowerCasePathSegments().get(0).equals(mSegment);
    }

    @Nullable @Override public Route fire(AnalysedUri uri) {
        return this;
    }


    @Override public int whichPage() {
        return PAGE;
    }

    @NonNull
    @Override public BaseContentFragment getFragment() {
        return MainContentFragment.instantiate();
    }

    @Override public boolean isAlwaysBellow() {
        return true;
    }

    @NonNull @Override public Uri.Builder addPath(
            @NonNull Uri.Builder builder,
            @NonNull Resources resources
    ) {
        return builder.appendPath(mSegment);
    }
}
