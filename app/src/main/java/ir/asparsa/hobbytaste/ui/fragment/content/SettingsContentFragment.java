package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import ir.asparsa.hobbytaste.R;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public class SettingsContentFragment extends BaseContentFragment {

    private static final String FRAGMENT_TAG = "settings";

    public static SettingsContentFragment instantiate() {

        Bundle bundle = new Bundle();

        SettingsContentFragment fragment = new SettingsContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override protected String setHeaderTitle() {
        return getString(R.string.title_settings);
    }

    @Override public String getFragmentTag() {
        return FRAGMENT_TAG;
    }

    @Override public BackState onBackPressed() {
        return BackState.CLOSE_APP;
    }

}
