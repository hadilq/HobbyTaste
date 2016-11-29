package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.fragment.BaseFragment;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public abstract class BaseContentFragment extends BaseFragment {

    public static final String BUNDLE_KEY_HEADER_TITLE = "BUNDLE_KEY_HEADER_TITLE";

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArguments().putString(BUNDLE_KEY_HEADER_TITLE, setHeaderTitle());
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_fragment, container, false);
    }

    protected abstract String setHeaderTitle();

    public String getHeaderTitle() {
        return getArguments().getString(BUNDLE_KEY_HEADER_TITLE, "");
    }

    public abstract String getFragmentTag();

    public BackState onBackPressed() {
        return BackState.BACK_FRAGMENT;
    }

    public enum BackState {
        CLOSE_APP,
        BACK_FRAGMENT
    }

}
