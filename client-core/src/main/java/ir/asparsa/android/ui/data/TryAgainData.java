package ir.asparsa.android.ui.data;


import ir.asparsa.android.R;
import ir.asparsa.android.ui.view.TryAgainView;

/**
 * @author hadi
 * @since 7/25/2016 AD
 */
public class TryAgainData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.try_again;

    private boolean mStart;
    private TryAgainView.OnTryAgainListener mOnTryAgainListener;
    private String mErrorMessage;

    public TryAgainData(boolean s, TryAgainView.OnTryAgainListener t) {
        this.mStart = s;
        this.mOnTryAgainListener = t;
    }

    public void setErrorMessage(String mErrorMessage) {
        this.mErrorMessage = mErrorMessage;
    }

    public boolean start() {
        return mStart;
    }

    public TryAgainView.OnTryAgainListener getOnTryAgainListener() {
        return mOnTryAgainListener;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    @Override public int getViewType() {
        return VIEW_TYPE;
    }
}
