package ir.asparsa.hobbytaste.ui.fragment.dialog;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.dialog.BaseBottomDialogFragment;
import ir.asparsa.android.ui.view.DialogControlLayout;
import ir.asparsa.common.net.dto.AuthenticateDto;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.net.UserService;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import java.security.SecureRandom;

/**
 * Created by hadi on 12/15/2016 AD.
 */
public class SetUsernameDialogFragment extends BaseBottomDialogFragment {

    public static final String BUNDLE_KEY_USERNAME = "BUNDLE_KEY_STORE";

    @Inject
    UserService mUserService;

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

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.dialog_set_username, container, false);
        ButterKnife.bind(this, v);

        mController.setCommitText(getString(R.string.commit))
                   .arrange();
        mController.setOnControlListener(new DialogControlLayout.OnControlListener() {
            @Override public void onCommit() {
                actionChangeUsername(mUsernameEditText.getText().toString());
            }

            @Override public void onNeutral() {
            }

            @Override public void onCancel() {
            }
        });

        return v;
    }

    private void actionChangeUsername(final String username) {
        if (TextUtils.isEmpty(username)) {
            mUsernameLayout.setError(getString(R.string.username_is_empty));
            return;
        }
        mUserService.changeUsername(
                username,
                (System.currentTimeMillis() ^ (((long) username.hashCode()) << 31)) ^ (new SecureRandom().nextLong()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<AuthenticateDto>() {
                        @Override public void onCompleted() {
                        }

                        @Override public void onError(Throwable e) {
                            L.e(SetUsernameDialogFragment.class, "Cannot send comment", e);
                            mUsernameLayout.setError(e.getLocalizedMessage());
                        }

                        @Override public void onNext(AuthenticateDto authenticateDto) {
                            OnSetUsernameDialogResultEvent event
                                    = (OnSetUsernameDialogResultEvent) getOnDialogResultEvent();
                            event.setUsername(username);
                            event.setToken(authenticateDto.getToken());
                            event.setDialogResult(DialogResult.COMMIT);
                            sendEvent();
                            dismiss();
                        }
                    });
    }


    public static class OnSetUsernameDialogResultEvent extends BaseOnDialogResultEvent {

        private String username;
        private String token;

        public OnSetUsernameDialogResultEvent(@NonNull String sourceTag) {
            super(sourceTag);
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
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
            dest.writeString(this.token);
        }

        protected OnSetUsernameDialogResultEvent(Parcel in) {
            super(in);
            this.username = in.readString();
            this.token = in.readString();
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
