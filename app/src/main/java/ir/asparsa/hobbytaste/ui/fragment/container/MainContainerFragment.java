package ir.asparsa.hobbytaste.ui.fragment.container;

import android.os.Bundle;
import android.support.annotation.Nullable;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.MainContentFragment;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public class MainContainerFragment extends BaseContainerFragment {

    public static MainContainerFragment instantiate(int page) {

        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_PAGE, page);
        MainContainerFragment fragment = new MainContainerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BaseContentFragment fragment = NavigationUtil.getActiveContentFragment(getChildFragmentManager());
        if (fragment == null) {
            NavigationUtil.startContentFragment(getChildFragmentManager(), MainContentFragment.instantiate());
        }
    }
}
