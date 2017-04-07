package ir.asparsa.hobbytaste.ui.fragment.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ir.asparsa.hobbytaste.R;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public class AboutUsContentFragment extends BaseContentFragment {

    public static AboutUsContentFragment instantiate() {
        Bundle bundle = new Bundle();
        AboutUsContentFragment fragment = new AboutUsContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.about_us_fragment, container, false);
    }

    @Override protected String setHeaderTitle() {
        return getString(R.string.title_about_us);
    }

    @Override public BackState onBackPressed() {
        return BackState.BACK_FRAGMENT;
    }

    @Override public boolean hasHomeAsUp() {
        return true;
    }
}
