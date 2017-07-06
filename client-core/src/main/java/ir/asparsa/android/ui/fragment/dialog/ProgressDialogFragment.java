package ir.asparsa.android.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ir.asparsa.android.R;

/**
 * @author hadi
 */
public class ProgressDialogFragment extends BaseDialogFragment {

    private static final String BUNDLE_KEY_TITLE = "BUNDLE_KEY_TITLE";

    public static ProgressDialogFragment newInstance(@NonNull String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_TITLE, title);
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_progress, container, false);
        TextView titleTextView = (TextView) view.findViewById(R.id.title);

        String title = getArguments().getString(BUNDLE_KEY_TITLE);
        if (!TextUtils.isEmpty(title)) {
            titleTextView.setText(title);
        }
        return view;
    }
}
