package ir.asparsa.hobbytaste.ui.fragment.content;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;

/**
 * @author hadi
 * @since 4/18/2017 AD.
 */
public interface FragmentDelegate {
    Bundle getArguments();

    Context getContext();

    String getString(@StringRes int res);

    FragmentManager getFragmentManager();

    void onEvent(
            String event,
            Object... data
    );
}
