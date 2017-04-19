package ir.asparsa.hobbytaste.ui.mvp.holder;

import android.content.Context;
import android.os.Bundle;

/**
 * @author hadi
 * @since 4/18/2017 AD.
 */
public interface FragmentHolder {
    Bundle getArguments();

    Context getContext();

    void onClick(
            String event,
            Object... data
            );
}
