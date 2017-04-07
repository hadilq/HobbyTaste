package ir.asparsa.android.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.R;
import ir.asparsa.android.R2;

/**
 * @author hadi
 * @since 3/6/2017 AD.
 */
public class LoadingProgressDialogFragment extends BaseDialogFragment {

    private static final String BUNDLE_KEY_TITLE = "BUNDLE_KEY_TITLE";

    @BindView(R2.id.title)
    TextView mTitleTextView;
    @BindView(R2.id.progress_bar)
    ProgressBar mProgressBar;

    public static LoadingProgressDialogFragment newInstance(@NonNull String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_TITLE, title);
        LoadingProgressDialogFragment fragment = new LoadingProgressDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_loading_progress, container, false);
        ButterKnife.bind(this, view);

        String title = getArguments().getString(BUNDLE_KEY_TITLE);
        if (!TextUtils.isEmpty(title)) {
            mTitleTextView.setText(title);
        }
        mProgressBar.setProgress(0);
        return view;
    }

    public void setProgress(int percentage) {
        if (mProgressBar != null) {
            mProgressBar.setProgress(percentage);
        }
    }
}
