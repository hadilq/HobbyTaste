package ir.asparsa.hobbytaste.ui.mvp.holder;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;

/**
 * @author hadi
 * @since 4/18/2017 AD.
 */
public interface FragmentHolder {
    Bundle getArguments();

    Context getContext();

    String getString(@StringRes int res);

    FragmentManager getFragmentManager();

    void onClick(
            String event,
            Object... data
    );
}
