package ir.asparsa.hobbytaste.core.route;

import android.support.annotation.NonNull;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.MainContentFragment;

/**
 * @author hadi
 * @since 4/25/2017 AD.
 */
public class MainRoute implements Route {

    public static final String PATH_SEGMENT = "main";
    public static final int PAGE = 0;

    @Override public boolean shouldFire(AnalysedUri uri) {
        return uri.getLowerCasePathSegments().size() > 1 && uri.getLowerCasePathSegments().get(0).equals(PATH_SEGMENT);
    }

    @Override public Route fire(AnalysedUri uri) {
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
}
