package ir.asparsa.android.ui.holder;

import android.text.TextUtils;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.R2;
import ir.asparsa.android.ui.data.TryAgainData;
import ir.asparsa.android.ui.view.TryAgainView;

/**
 * @author hadi
 * @since 7/25/2016 AD
 */
public class TryAgainViewHolder extends BaseViewHolder<TryAgainData> {

    @BindView(R2.id.try_again_view)
    TryAgainView mTryAgainView;

    public TryAgainViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override public void onBindView(TryAgainData data) {
        mTryAgainView.setTryAgainListener(data.getOnTryAgainListener());
        if (data.start()) {
            mTryAgainView.start();
        } else if (!TextUtils.isEmpty(data.getErrorMessage())) {
            mTryAgainView.onError(data.getErrorMessage());
        }

    }
}
