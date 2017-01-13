package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.dialog.BaseDialogFragment;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.fragment.dialog.CommentDialogFragment;
import ir.asparsa.hobbytaste.ui.fragment.recycler.StoreDetailsRecyclerFragment;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public class StoreDetailsContentFragment extends BaseContentFragment {

    public static StoreDetailsContentFragment instantiate(StoreModel store) {

        Bundle bundle = new Bundle();
        bundle.putParcelable(StoreDetailsRecyclerFragment.BUNDLE_KEY_STORE, store);
        StoreDetailsContentFragment fragment = new StoreDetailsContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Fragment fragment = NavigationUtil.getActiveFragment(getChildFragmentManager());
        if (!(fragment instanceof StoreDetailsRecyclerFragment)) {

            NavigationUtil.startNestedFragment(
                    getChildFragmentManager(),
                    StoreDetailsRecyclerFragment.instantiate(new Bundle(getArguments()))
            );
        }
    }

    @Override protected String setHeaderTitle() {
        return getString(R.string.title_store_details);
    }

    @Override public BackState onBackPressed() {
        return BackState.BACK_FRAGMENT;
    }

    @Override public void onEvent(BaseEvent event) {
        L.i(getClass(), "event received: ");
        if (event instanceof BaseDialogFragment.BaseOnDialogResultEvent &&
            ((BaseDialogFragment.BaseOnDialogResultEvent) event).getDialogResult() !=
            BaseDialogFragment.DialogResult.COMMIT) {
            return;
        }

        if (event instanceof CommentDialogFragment.CommentDialogResultEvent) {
            CommentDialogFragment.CommentDialogResultEvent commentEvent
                    = (CommentDialogFragment.CommentDialogResultEvent) event;

            Fragment fragment = NavigationUtil.getActiveFragment(getChildFragmentManager());
            if (fragment instanceof StoreDetailsRecyclerFragment) {
                ((StoreDetailsRecyclerFragment) fragment).addComment(commentEvent.getComment());
            }
        }
    }

    @Override public FloatingActionButtonObserver getFloatingActionButtonObserver() {
        return new FloatingActionButtonObserver() {
            @Override public void onNext(View view) {
                StoreModel store = getArguments().getParcelable(StoreDetailsRecyclerFragment.BUNDLE_KEY_STORE);
                CommentDialogFragment
                        .instantiate(store, new CommentDialogFragment.CommentDialogResultEvent(getTagName()))
                        .show(getFragmentManager());
            }
        };
    }
}
