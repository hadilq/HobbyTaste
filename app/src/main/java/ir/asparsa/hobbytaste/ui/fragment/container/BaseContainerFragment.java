package ir.asparsa.hobbytaste.ui.fragment.container;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.activity.LaunchActivity;
import ir.asparsa.hobbytaste.ui.fragment.BaseFragment;
import junit.framework.Assert;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public abstract class BaseContainerFragment extends BaseFragment {

    public static final String BUNDLE_KEY_PAGE = "BUNDLE_KEY_PAGE";

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.container_fragment, container, false);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentActivity activity = getActivity();
        if (activity instanceof LaunchActivity) {
            ((LaunchActivity) activity).addContainer(this, getArguments().getInt(BUNDLE_KEY_PAGE));
        } else {
            Assert.fail("No other activity than LaunchActivity is allowed");
        }
    }
}
