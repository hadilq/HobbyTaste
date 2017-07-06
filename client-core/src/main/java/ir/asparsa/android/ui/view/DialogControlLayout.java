package ir.asparsa.android.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.R;
import ir.asparsa.android.R2;

/**
 * @author hadi
 */
public class DialogControlLayout extends RelativeLayout {

    @BindView(R2.id.commit)
    Button mCommitButton;
    @BindView(R2.id.neutral)
    Button mNeutralButton;
    @BindView(R2.id.cancel)
    Button mCancelButton;

    private OnControlListener mOnControlListener;

    public DialogControlLayout(Context context) {
        super(context);
        initiate();
    }

    public DialogControlLayout(
            Context context,
            AttributeSet attrs
    ) {
        super(context, attrs);
        initiate();
    }

    private void initiate() {
        LayoutInflater.from(getContext()).inflate(R.layout.dialog_control_include, this);

        ButterKnife.bind(this);

        mCommitButton.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View view) {
                if (mOnControlListener != null) {
                    mOnControlListener.onCommit();
                }
            }
        });

        mNeutralButton.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View view) {
                if (mOnControlListener != null) {
                    mOnControlListener.onNeutral();
                }
            }
        });

        mCancelButton.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View view) {
                if (mOnControlListener != null) {
                    mOnControlListener.onCancel();
                }
            }
        });
    }

    public DialogControlLayout setCommitText(CharSequence commit) {
        mCommitButton.setText(commit);
        return this;
    }

    public DialogControlLayout setNeutralText(CharSequence neutral) {
        mNeutralButton.setText(neutral);
        return this;
    }

    public DialogControlLayout setCancelText(CharSequence cancel) {
        mCancelButton.setText(cancel);
        return this;
    }

    public void arrange() {
        if (TextUtils.isEmpty(mCommitButton.getText())) {
            mCommitButton.setVisibility(INVISIBLE);
        }

        if (TextUtils.isEmpty(mNeutralButton.getText())) {
            mNeutralButton.setVisibility(INVISIBLE);
        }

        if (TextUtils.isEmpty(mCancelButton.getText())) {
            mCancelButton.setVisibility(INVISIBLE);
        }
    }

    public void setOnControlListener(OnControlListener mOnControlListener) {
        this.mOnControlListener = mOnControlListener;
    }

    public interface OnControlListener {
        void onCommit();

        void onNeutral();

        void onCancel();
    }

}
