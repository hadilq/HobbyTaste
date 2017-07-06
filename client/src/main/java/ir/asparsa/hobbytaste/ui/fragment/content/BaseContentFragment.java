package ir.asparsa.hobbytaste.ui.fragment.content;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.*;
import ir.asparsa.android.ui.fragment.BaseFragment;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.route.RouteFactory;
import ir.asparsa.hobbytaste.ui.activity.LaunchActivity;
import rx.Observer;

import javax.inject.Inject;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public abstract class BaseContentFragment extends BaseFragment {

    public static final String BUNDLE_KEY_HEADER_TITLE = "BUNDLE_KEY_HEADER_TITLE";
    public static final String BUNDLE_KEY_FRAGMENT_POSITION = "BUNDLE_KEY_FRAGMENT_POSITION";

    @Inject
    RouteFactory mRouteFactory;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getArguments().putString(BUNDLE_KEY_HEADER_TITLE, setHeaderTitle());
        ApplicationLauncher.mainComponent().inject(this);
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.content_fragment, container, false);
    }

    @Override public void onCreateOptionsMenu(
            Menu menu,
            MenuInflater inflater
    ) {
        if (mRouteFactory.getCurrentPosition() ==
            getArguments().getInt(BUNDLE_KEY_FRAGMENT_POSITION, RouteFactory.NO_POSITION)) {
            menu.clear();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected abstract String setHeaderTitle();

    public String getHeaderTitle() {
        return getArguments().getString(BUNDLE_KEY_HEADER_TITLE, "");
    }

    public void setCurrentPosition(int position) {
        getArguments().putInt(BUNDLE_KEY_FRAGMENT_POSITION, position);
    }

    public BackState onBackPressed() {
        return BackState.BACK_FRAGMENT;
    }

    public FloatingActionButtonObserver getFloatingActionButtonObserver() {
        return null;
    }

    public boolean hasHomeAsUp() {
        return false;
    }

    public boolean scrollToolbar() {
        return false;
    }

    public static abstract class FloatingActionButtonObserver implements Observer<View> {
        @Override public void onCompleted() {
        }

        @Override public void onError(Throwable e) {
        }
    }

    protected LaunchActivity activity() {
        FragmentActivity activity = getActivity();
        if (activity instanceof LaunchActivity) {
            return (LaunchActivity) activity;
        }
        throw new RuntimeException("Activity is not launch activity");
    }

    public enum BackState {
        CLOSE_APP,
        BACK_FRAGMENT
    }

    protected FragmentDelegate getDelegate() {
        return new FragmentDelegate() {
            @Override public Bundle getArguments() {
                return BaseContentFragment.this.getArguments();
            }

            @Override public Context getContext() {
                return BaseContentFragment.this.getContext();
            }

            @Override public String getString(@StringRes int res) {
                return BaseContentFragment.this.getString(res);
            }

            @Override public FragmentManager getFragmentManager() {
                return BaseContentFragment.this.getFragmentManager();
            }

            @Override public void onEvent(
                    String event,
                    Object... data
            ) {
                BaseContentFragment.this.onEvent(event, data);
            }
        };
    }

    protected void onEvent(
            String event,
            Object... data
    ) {

    }

}
