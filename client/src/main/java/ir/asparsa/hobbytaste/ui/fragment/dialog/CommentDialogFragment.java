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
import ir.asparsa.android.ui.fragment.dialog.BaseDialogFragment;
import ir.asparsa.android.ui.view.DialogControlLayout;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.manager.AuthorizationManager;
import ir.asparsa.hobbytaste.core.manager.CommentManager;
import ir.asparsa.hobbytaste.database.model.CommentModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import rx.Observer;
import rx.subscriptions.CompositeSubscription;

import javax.inject.Inject;

/**
 * Created by hadi on 12/15/2016 AD.
 */
public class CommentDialogFragment extends BaseDialogFragment {

    public static final String BUNDLE_KEY_STORE = "BUNDLE_KEY_STORE";

    @Inject
    CommentManager mCommentManager;
    @Inject
    AuthorizationManager mAuthorizationManager;

    @BindView(R.id.title)
    TextView mTitleTextView;
    @BindView(R.id.input_layout_comment)
    TextInputLayout mCommentLayout;
    @BindView(R.id.input_comment)
    EditText mCommentEditText;
    @BindView(R.id.controller)
    DialogControlLayout mController;

    private CompositeSubscription mSubscription = new CompositeSubscription();

    public static CommentDialogFragment instantiate(
            StoreModel store,
            @NonNull CommentDialogResultEvent event
    ) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_KEY_STORE, store);
        CommentDialogFragment fragment = new CommentDialogFragment();
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
        View v = inflater.inflate(R.layout.dialog_comment, container, false);
        ButterKnife.bind(this, v);

        mController.setCommitText(getString(R.string.commit))
                   .arrange();
        mController.setOnControlListener(new DialogControlLayout.OnControlListener() {
            @Override public void onCommit() {
                actionComment(mCommentEditText.getText().toString());
            }

            @Override public void onNeutral() {
            }

            @Override public void onCancel() {
            }
        });

        return v;
    }

    private void actionComment(final String description) {
        if (TextUtils.isEmpty(description)) {
            mCommentLayout.setError(getString(R.string.comment_is_empty));
            return;
        }
        StoreModel store = getArguments().getParcelable(BUNDLE_KEY_STORE);
        if (store == null) {
            mCommentLayout.setError(getString(R.string.comment_store_is_empty));
            return;
        }

        CommentModel comment = new CommentModel(description, mAuthorizationManager.getUsername(), store.getId());
        mSubscription.add(mCommentManager.saveComment(
                comment,
                new Observer<CommentModel>() {
                    @Override public void onCompleted() {
                    }

                    @Override public void onError(Throwable e) {
                        L.e(CommentDialogFragment.class, "Cannot send comment", e);
                        mCommentLayout.setError(getString(R.string.connection_error));
                    }

                    @Override public void onNext(CommentModel newComment) {
                        CommentDialogResultEvent event
                                = (CommentDialogResultEvent) getOnDialogResultEvent();
                        event.setDialogResult(DialogResult.COMMIT);
                        event.setComment(newComment);
                        sendEvent();
                        dismiss();
                    }
                }));
    }

    @Override public void onDestroyView() {
        mSubscription.clear();
        super.onDestroyView();
    }

    public static class CommentDialogResultEvent extends BaseOnDialogResultEvent {

        private CommentModel comment;

        public CommentDialogResultEvent(@NonNull String sourceTag) {
            super(sourceTag);
        }

        public CommentModel getComment() {
            return comment;
        }

        public void setComment(CommentModel comment) {
            this.comment = comment;
        }

        @Override public int describeContents() {
            return 0;
        }

        @Override public void writeToParcel(
                Parcel dest,
                int flags
        ) {
            super.writeToParcel(dest, flags);
        }

        protected CommentDialogResultEvent(Parcel in) {
            super(in);
        }

        public static final Creator<CommentDialogResultEvent> CREATOR = new Creator<CommentDialogResultEvent>() {
            @Override public CommentDialogResultEvent createFromParcel(Parcel source) {
                return new CommentDialogResultEvent(source);
            }

            @Override public CommentDialogResultEvent[] newArray(int size) {
                return new CommentDialogResultEvent[size];
            }
        };
    }

}
