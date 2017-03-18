package ir.asparsa.hobbytaste.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.ui.view.DialogControlLayout;
import ir.asparsa.common.net.dto.FeedbackDto;
import ir.asparsa.common.net.dto.ResponseDto;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.net.FeedbackService;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import javax.inject.Inject;

/**
 * @author hadi
 * @since 3/18/2017 AD.
 */
public class CrashReportActivity extends BaseActivity {

    public static final String BUNDLE_KEY_CRASH_MESSAGE = "BUNDLE_KEY_CRASH_MESSAGE";
    public static final String BUNDLE_KEY_CRASH_THROWABLE_NAME = "BUNDLE_KEY_CRASH_THROWABLE_NAME";

    @BindView(R.id.feedback_text)
    EditText mFeedback;
    @BindView(R.id.controller)
    DialogControlLayout mController;
    @BindView(R.id.error)
    TextView mError;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @Inject
    FeedbackService mFeedbackService;

    private CompositeSubscription mSubscription = new CompositeSubscription();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_report_activity);
        ButterKnife.bind(this);
        ApplicationLauncher.mainComponent().inject(this);

        mController.setCommitText(getString(R.string.send)).arrange();
        mController.setOnControlListener(new DialogControlLayout.OnControlListener() {
            @Override public void onCommit() {
                actionFeedback(mFeedback.getText().toString());
            }

            @Override public void onNeutral() {
            }

            @Override public void onCancel() {
            }
        });
    }

    @Override protected void onDestroy() {
        mSubscription.clear();
        super.onDestroy();
    }

    private void actionFeedback(String feedback) {
        if (TextUtils.isEmpty(feedback)) {
            mError.setText(getString(R.string.crash_report_is_empty));
            mError.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            return;
        }
        String crashThrowableName = getIntent().getStringExtra(BUNDLE_KEY_CRASH_THROWABLE_NAME);
        String crashMessage = getIntent().getStringExtra(BUNDLE_KEY_CRASH_MESSAGE);

        mError.setText("");
        mError.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        mSubscription.add(
                mFeedbackService
                        .feedback(new FeedbackDto(feedback, crashThrowableName, crashMessage))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ResponseDto>() {
                            @Override public void call(ResponseDto responseDto) {
                                mError.setVisibility(View.GONE);
                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(
                                        CrashReportActivity.this, R.string.crash_report_successfully_sent,
                                        Toast.LENGTH_LONG).show();
                                CrashReportActivity.this.finish();
                            }
                        }, new Action1<Throwable>() {
                            @Override public void call(Throwable throwable) {
                                mError.setVisibility(View.VISIBLE);
                                mError.setText(throwable.getLocalizedMessage());
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }));
    }
}
