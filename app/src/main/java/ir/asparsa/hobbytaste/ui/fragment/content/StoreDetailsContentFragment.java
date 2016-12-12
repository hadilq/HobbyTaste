package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.fragment.recycler.StoreDetailsRecyclerFragment;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public class StoreDetailsContentFragment extends BaseContentFragment {

    private static final String FRAGMENT_TAG = "store-details";

    public static StoreDetailsContentFragment instantiate(StoreModel store) {

        Bundle bundle = new Bundle();
        bundle.putParcelable(StoreDetailsRecyclerFragment.BUNDLE_KEY_STORE, store);
        StoreDetailsContentFragment fragment = new StoreDetailsContentFragment();
        fragment.setArguments(bundle);
        return fragment;
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

    @Override public String getFragmentTag() {
        return FRAGMENT_TAG;
    }

    @Override public BackState onBackPressed() {
        return BackState.BACK_FRAGMENT;
    }

}
