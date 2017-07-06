package ir.asparsa.hobbytaste.core.route;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.PlacesContentFragment;

/**
 * @author hadi
 * @since 4/28/2017 AD.
 */
public class PlacesRoute implements Route {

    public static final int PAGE = 0;
    public static final String LAT = "lat";
    public static final String LNG = "lng";

    private final String mSegment;
    private long mLat = Long.MIN_VALUE;
    private long mLng = Long.MIN_VALUE;

    PlacesRoute(Resources resources) {
        ApplicationLauncher.mainComponent().inject(this);
        mSegment = resources.getString(R.string.path_segment_places);
    }

    @Override public boolean shouldFire(AnalysedUri uri) {
        return uri.getLowerCasePathSegments().size() == 1 && uri.getLowerCasePathSegments().get(0).equals(mSegment);
    }

    @Nullable @Override public Route fire(AnalysedUri uri) {
        try {
            mLat = Long.parseLong(uri.getQueryParameter(LAT));
            mLng = Long.parseLong(uri.getQueryParameter(LNG));
        } catch (NumberFormatException e) {
            return null;
        }
        return this;
    }

    @Override public int whichPage() {
        return PAGE;
    }

    @NonNull @Override public BaseContentFragment getFragment() {
        return PlacesContentFragment.instantiate(mLat, mLng);
    }

    @Override public boolean isAlwaysBellow() {
        return false;
    }

    @NonNull @Override public Uri.Builder addPath(
            @NonNull Uri.Builder builder,
            @NonNull Resources resources
    ) {
        return builder.appendPath(mSegment);
    }
}
