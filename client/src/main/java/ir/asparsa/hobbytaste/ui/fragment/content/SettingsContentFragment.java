package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.ui.fragment.recycler.SettingsRecyclerFragment;
import ir.asparsa.hobbytaste.ui.list.holder.AboutUsViewHolder;
import rx.Observer;

import javax.inject.Inject;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public class SettingsContentFragment extends BaseContentFragment {

    @Inject
    NavigationUtil mNavigationUtil;

    public static SettingsContentFragment instantiate() {

        Bundle bundle = new Bundle();

        SettingsContentFragment fragment = new SettingsContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Fragment fragment = mNavigationUtil.getActiveFragment(getChildFragmentManager());
        SettingsRecyclerFragment settingsRecyclerFragment;

        if (!(fragment instanceof SettingsRecyclerFragment)) {
            settingsRecyclerFragment = SettingsRecyclerFragment.instantiate();

            mNavigationUtil.startNestedFragment(
                    getChildFragmentManager(),
                    settingsRecyclerFragment
            );
        } else {
            settingsRecyclerFragment = (SettingsRecyclerFragment) fragment;
        }

        settingsRecyclerFragment.setContentObserver(geRecyclerObserver());
    }

    private <T> Observer<T> geRecyclerObserver() {
        return new Observer<T>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(T t) {
                if (t instanceof AboutUsViewHolder.AboutUsClick) {
                    onAboutUsClick();
                }
            }
        };
    }

    @Override protected String setHeaderTitle() {
        return getString(R.string.title_settings);
    }

    @Override public BackState onBackPressed() {
        return BackState.CLOSE_APP;
    }

    private void onAboutUsClick() {
        mNavigationUtil.startContentFragment(getFragmentManager(), AboutUsContentFragment.instantiate());
    }

}
