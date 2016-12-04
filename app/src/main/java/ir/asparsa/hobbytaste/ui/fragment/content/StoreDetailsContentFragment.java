package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.database.model.StoreModel;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public class StoreDetailsContentFragment extends BaseContentFragment {

    private static final String BUNDLE_KEY_STORE = "BUNDLE_KEY_STORE";
    private static final String FRAGMENT_TAG = "store-details";

    public static StoreDetailsContentFragment instantiate(StoreModel store ) {

        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_KEY_STORE, store);
        StoreDetailsContentFragment fragment = new StoreDetailsContentFragment();
        fragment.setArguments(bundle);
        return fragment;
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
