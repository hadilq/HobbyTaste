package ir.asparsa.android.ui.fragment.dialog;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import ir.asparsa.android.ui.fragment.BaseFragment;

/**
 * @author hadi
 */
public interface IDialogFragment {

    String BUNDLE_KEY_DIALOG_RESULT_EVENT = "BUNDLE_KEY_DIALOG_RESULT_EVENT";

    void setOnDialogResultEvent(BaseOnDialogResultEvent event);

    void show(FragmentManager manager);

    abstract class BaseOnDialogResultEvent extends BaseFragment.BaseEvent {

        private int mDialogResult = -1;

        public BaseOnDialogResultEvent(@NonNull String sourceTag) {
            super(sourceTag);
        }

        public int getDialogResult() {
            return mDialogResult;
        }

        public void setDialogResult(int dialogResult) {
            this.mDialogResult = dialogResult;
        }


        @Override public int describeContents() {
            return 0;
        }

        @Override public void writeToParcel(
                Parcel dest,
                int flags
        ) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.mDialogResult);
        }

        protected BaseOnDialogResultEvent(Parcel in) {
            super(in);
            this.mDialogResult = in.readInt();
        }
    }

    interface DialogResult {
        int COMMIT = 0;
        int NEUTRAL = 1;
        int CANCEL = 2;
    }

}
