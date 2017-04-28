package ir.asparsa.hobbytaste.core.route;

import android.support.annotation.NonNull;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.SettingsContentFragment;

/**
 * @author hadi
 * @since 4/25/2017 AD.
 */
public class SettingsRoute implements Route {

    public static final String PATH_SEGMENT = "settings";
    public static final int PAGE = 1;

    @Override public boolean shouldFire(AnalysedUri uri) {
        return uri.getLowerCasePathSegments().size() >= 1 && uri.getLowerCasePathSegments().get(0).equals(PATH_SEGMENT);
    }

    @NonNull @Override public Route fire(AnalysedUri uri) {
        return this;
    }

    @Override public int whichPage() {
        return PAGE;
    }

    @NonNull @Override public BaseContentFragment getFragment() {
        return SettingsContentFragment.instantiate();
    }

    @Override public boolean isAlwaysBellow() {
        return true;
    }
}
