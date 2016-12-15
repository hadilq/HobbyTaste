package ir.asparsa.hobbytaste.ui.fragment.dialog;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.ui.fragment.dialog.BaseDialogFragment;
import ir.asparsa.android.ui.view.DialogControlLayout;
import ir.asparsa.hobbytaste.R;

/**
 * Created by hadi on 12/15/2016 AD.
 */
public class SetUsernameDialogFragment extends BaseDialogFragment {

    public static final String BUNDLE_KEY_USERNAME = "BUNDLE_KEY_USERNAME";

    @BindView(R.id.title)
    TextView mTitleTextView;
    @BindView(R.id.input_layout_username)
    TextInputLayout mUsernameLayout;
    @BindView(R.id.input_username)
    EditText mUsernameEditText;
    @BindView(R.id.controller)
    DialogControlLayout mController;

    public static SetUsernameDialogFragment instantiate(
            String username,
            @NonNull OnSetUsernameDialogResultEvent event
    ) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_USERNAME, username);
        SetUsernameDialogFragment fragment = new SetUsernameDialogFragment();
        fragment.setArguments(bundle);
        fragment.setOnDialogResultEvent(event);
        return fragment;
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.set_username_dialog, container, false);
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(params);

        ButterKnife.bind(this, v);

        mController.setCommitText(getString(R.string.commit))
                   .arrange();
        mController.setOnControlListener(new DialogControlLayout.OnControlListener() {
            @Override public void onCommit() {
                getOnDialogResultEvent().setDialogResult(DialogResult.COMMIT);
                sendEvent();
                dismiss();
            }

            @Override public void onNeutral() {
                getOnDialogResultEvent().setDialogResult(DialogResult.NEUTRAL);
                sendEvent();
                dismiss();
            }

            @Override public void onCancel() {
                getOnDialogResultEvent().setDialogResult(DialogResult.CANCEL);
                sendEvent();
                dismiss();
            }
        });

        return v;
    }

    public static class OnSetUsernameDialogResultEvent extends BaseOnDialogResultEvent {

        private String username;

        public OnSetUsernameDialogResultEvent(@NonNull String sourceTag) {
            super(sourceTag);
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }


        @Override public int describeContents() {
            return 0;
        }

        @Override public void writeToParcel(
                Parcel dest,
                int flags
        ) {
            super.writeToParcel(dest, flags);
            dest.writeString(this.username);
        }

        protected OnSetUsernameDialogResultEvent(Parcel in) {
            super(in);
            this.username = in.readString();
        }

        public static final Creator<OnSetUsernameDialogResultEvent> CREATOR
                = new Creator<OnSetUsernameDialogResultEvent>() {
            @Override public OnSetUsernameDialogResultEvent createFromParcel(Parcel source) {
                return new OnSetUsernameDialogResultEvent(source);
            }

            @Override public OnSetUsernameDialogResultEvent[] newArray(int size) {
                return new OnSetUsernameDialogResultEvent[size];
            }
        };
    }

}
