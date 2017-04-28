package ir.asparsa.hobbytaste.core.route;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;

/**
 * @author hadi
 * @since 4/25/2017 AD.
 */
public interface Route {

    boolean shouldFire(AnalysedUri uri);

    @Nullable
    Route fire(AnalysedUri uri);

    int whichPage();

    @NonNull
    BaseContentFragment getFragment();

    boolean isAlwaysBellow();
}
