package ir.asparsa.hobbytaste.ui.mvp.holder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.ui.fragment.dialog.LoadingProgressDialogFragment;
import ir.asparsa.android.ui.fragment.dialog.ProgressDialogFragment;
import ir.asparsa.android.ui.view.DialogControlLayout;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.mvp.presenter.AddBannerPresenter;
import rx.functions.Action1;

import java.io.File;


/**
 * @author hadi
 * @since 4/23/2017 AD.
 */
public class AddBannerViewHolder implements ViewHolder {

    private final View mView;
    private final AddBannerPresenter mPresenter;

    @BindView(R.id.banner)
    ImageView mBannerImageView;
    @BindView(R.id.hint)
    TextView mHintTextView;
    @BindView(R.id.controller)
    DialogControlLayout mController;

    private ProgressDialogFragment mProgressDialog;
    private LoadingProgressDialogFragment mLoadingProgressDialog;
    private Handler mHandler;
    private Runnable mShowProgressDialogRunnable;

    public AddBannerViewHolder(
            View view,
            AddBannerPresenter presenter
    ) {
        mView = view;
        mPresenter = presenter;
        ButterKnife.bind(this, view);
        mHandler = new Handler(Looper.getMainLooper());

        mBannerImageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mPresenter.chooseImage();
            }
        });
    }

    public void setupControllerButtons(
            boolean hasFinishButton,
            boolean hasNextButton,
            DialogControlLayout.OnControlListener listener
    ) {
        if (hasFinishButton) {
            mController.setCommitText(mView.getContext().getString(R.string.finish));
            mHintTextView.setText(mView.getContext().getString(R.string.new_store_banner_choose_banner));
        }
        if (hasNextButton) {
            mController.setNeutralText(mView.getContext().getString(R.string.next));
        }

        mController.setCancelText(mView.getContext().getString(R.string.send));
        mController.arrange();
        mController.setOnControlListener(listener);
    }

    public void setImageBitmap(@NonNull Bitmap bitmap) {
        mBannerImageView.setImageBitmap(bitmap);
    }

    public void setImageBitmap(@NonNull String filePath) {
        setImageBitmap(BitmapFactory.decodeFile(
                new File(filePath).getAbsolutePath(),
                new BitmapFactory.Options()));
    }

    public void setHintText(String string) {
        mHintTextView.setText(string);
    }

    public void setHintText(@StringRes int string) {
        mHintTextView.setText(string);
    }

    public Action1<Integer> getProgressObserver() {
        return new Action1<Integer>() {
            @Override public void call(Integer percentage) {
                if (mLoadingProgressDialog != null) {
                    mLoadingProgressDialog.setProgress(percentage);
                }
            }
        };
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isAdded()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }
    }

    public void showLoadingProgressDialog(
            @StringRes final int message,
            FragmentManager manager
    ) {
        dismissLoadingProgressDialog();

        mLoadingProgressDialog = LoadingProgressDialogFragment.newInstance(mView.getContext().getString(message));
        mLoadingProgressDialog.show(manager);
    }

    public void showProgressDialog(
            @StringRes final int message,
            FragmentManager manager
    ) {
        dismissProgressDialog();

        mProgressDialog = ProgressDialogFragment.newInstance(mView.getContext().getString(message));
        mShowProgressDialogRunnable = new ShowProgressDialogRunnable(manager);
    }

    public void dismissLoadingProgressDialog() {
        if (mLoadingProgressDialog != null) {
            if (mShowProgressDialogRunnable != null) {
                mHandler.removeCallbacks(mShowProgressDialogRunnable);
                mShowProgressDialogRunnable = null;
            }
            if (mLoadingProgressDialog.isAdded()) {
                mLoadingProgressDialog.dismiss();
            }
            mLoadingProgressDialog = null;
        }
    }

    private class ShowProgressDialogRunnable implements Runnable {
        private final FragmentManager fragmentManager;

        ShowProgressDialogRunnable(FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
        }

        @Override public void run() {
            if (mProgressDialog != null) {
                mProgressDialog.show(fragmentManager);
            }
        }
    }
}
