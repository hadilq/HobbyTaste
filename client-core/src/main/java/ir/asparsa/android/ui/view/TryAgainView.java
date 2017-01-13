package ir.asparsa.android.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.R;
import ir.asparsa.android.R2;

/**
 * @author hadi
 * @since 6/24/2016 AD
 */
public class TryAgainView extends RelativeLayout {

    @BindView(R2.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R2.id.try_again_layout)
    RelativeLayout mOnTryAgainLayout;
    @BindView(R2.id.try_again_message)
    TextView mMessageTextView;
    @BindView(R2.id.try_again_button)
    ImageView mTryButton;
    @BindView(R2.id.extra_layout)
    FrameLayout mExtraLayout;

    private OnTryAgainListener mOnTryAgainListener;

    public TryAgainView(Context context) {
        super(context);
        initiate();
    }

    public TryAgainView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initiate();
    }

    private void initiate() {
        LayoutInflater.from(getContext()).inflate(R.layout.try_again_include, this);

        ButterKnife.bind(this);

        mTryButton.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                start();
                if (mOnTryAgainListener != null) {
                    mOnTryAgainListener.tryAgain();
                }
            }
        });

        mProgressBar.getIndeterminateDrawable().setColorFilter(
                getContext().getResources().getColor(R.color.progress_bar), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public void start() {
        mProgressBar.setVisibility(VISIBLE);
        mOnTryAgainLayout.setVisibility(GONE);
        mExtraLayout.setVisibility(View.GONE);
    }

    public void finish() {
        mProgressBar.setVisibility(GONE);
        mOnTryAgainLayout.setVisibility(GONE);
        mExtraLayout.setVisibility(View.GONE);
    }

    public void setTryAgainListener(OnTryAgainListener onTryAgainListener) {
        mOnTryAgainListener = onTryAgainListener;
    }

    public void onError(@Nullable String message) {
        if (!TextUtils.isEmpty(message)) {
            mMessageTextView.setText(message);
        }
        mProgressBar.setVisibility(GONE);
        mOnTryAgainLayout.setVisibility(VISIBLE);
    }

    public void setExtraView(@Nullable View view) {
        if (view != null) {
            mExtraLayout.addView(view, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        }
        mExtraLayout.setVisibility(View.GONE);
    }

    public void showExtraView() {
        mProgressBar.setVisibility(GONE);
        mOnTryAgainLayout.setVisibility(GONE);
        mExtraLayout.setVisibility(VISIBLE);
    }

    public interface OnTryAgainListener {
        void tryAgain();
    }

}
