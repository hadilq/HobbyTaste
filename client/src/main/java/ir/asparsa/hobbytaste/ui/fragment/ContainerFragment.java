package ir.asparsa.hobbytaste.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.core.util.UiUtil;
import ir.asparsa.android.ui.fragment.BaseFragment;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.route.Route;
import ir.asparsa.hobbytaste.core.route.RouteFactory;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.ui.activity.LaunchActivity;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;
import junit.framework.Assert;

import javax.inject.Inject;
import java.util.List;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public class ContainerFragment extends BaseFragment {

    private static final String BUNDLE_KEY_POSITION = "BUNDLE_KEY_POSITION";

    @Inject
    RouteFactory mRouteFactory;

    public static ContainerFragment instantiate(int pos) {

        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_POSITION, pos);
        ContainerFragment fragment = new ContainerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);
    }


    @Nullable @Override public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.container_fragment, container, false);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentActivity activity = getActivity();
        int pos = getArguments().getInt(BUNDLE_KEY_POSITION);
        if (activity instanceof LaunchActivity) {
            mRouteFactory.addContainer(((LaunchActivity) activity), this, pos);
        } else {
            Assert.fail("No other activity than LaunchActivity is allowed");
        }

        launchRoute(mRouteFactory.getContainerRoute(pos));
    }

    public void launchRoute(
            Route route
    ) {
        FragmentManager fragmentManager = getChildFragmentManager();
        BaseContentFragment fragment = NavigationUtil.getActiveContentFragment(fragmentManager);
        BaseContentFragment contentFragment = route.getFragment();
        if (fragment == null) {
            NavigationUtil.startContentFragment(fragmentManager, contentFragment);
        } else {
            if (route.isAlwaysBellow()) {
                if (findOrPopBaseContentFragment(fragmentManager, contentFragment) == null) {
                    NavigationUtil.startContentFragment(fragmentManager, contentFragment);
                }
            } else if (!fragment.getTagName().equals(contentFragment.getTagName())) {
                NavigationUtil.startContentFragment(fragmentManager, contentFragment);
            }
        }
    }

    @Nullable
    private BaseContentFragment findOrPopBaseContentFragment(
            @NonNull FragmentManager fragmentManager,
            @NonNull BaseContentFragment contentFragment
    ) {
        BaseContentFragment baseContentFragment = null;
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null) {
            return null;
        }
        for (int i = fragments.size() - 1; i >= 0; i--) {
            if (fragments.get(i) instanceof BaseContentFragment) {
                BaseContentFragment content = (BaseContentFragment) fragments.get(i);
                if (content.getTagName().equals(contentFragment.getTagName())) {
                    baseContentFragment = content;
                    break;
                } else {
                    try {
                        fragmentManager.popBackStack(content.getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    } catch (Exception e) {
                        L.e(UiUtil.class.getClass(), "Pop problem!", e);
                    }
                }
            }
        }
        return baseContentFragment;
    }
}
