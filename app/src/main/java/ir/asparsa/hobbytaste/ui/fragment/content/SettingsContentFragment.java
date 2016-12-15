package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.ui.fragment.recycler.SettingsRecyclerFragment;

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

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Fragment fragment = NavigationUtil.getActiveFragment(getChildFragmentManager());
        if (!(fragment instanceof SettingsRecyclerFragment)) {

            NavigationUtil.startNestedFragment(
                    getChildFragmentManager(),
                    SettingsRecyclerFragment.instantiate()
            );
        }
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
