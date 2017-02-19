package ir.asparsa.android.ui.fragment.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.BaseFragment;

import java.util.List;

/**
 * Created by hadi on 12/15/2016 AD.
 */
public abstract class BaseDialogFragment extends BottomSheetDialogFragment {

    private static final String BUNDLE_KEY_DIALOG_RESULT_EVENT = "BUNDLE_KEY_DIALOG_RESULT_EVENT";

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(params);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setOnDialogResultEvent(BaseOnDialogResultEvent event) {
        getArguments().putParcelable(BUNDLE_KEY_DIALOG_RESULT_EVENT, event);
    }

    protected BaseOnDialogResultEvent getOnDialogResultEvent() {
        return getArguments().getParcelable(BUNDLE_KEY_DIALOG_RESULT_EVENT);
    }

    protected void sendEvent() {
        BaseOnDialogResultEvent event = getOnDialogResultEvent();

        List<Fragment> fragments = getFragmentManager().getFragments();
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment instanceof BaseFragment && event.getSourceTag().equals(fragment.getTag())) {
                L.i(this.getClass(), "Find base fragment to send event: " + fragment.getClass().getName());
                ((BaseFragment) fragment).onEvent(event);
                break;
            }
        }
    }

    @Override public void onCancel(DialogInterface dialog) {
        getOnDialogResultEvent().setDialogResult(DialogResult.CANCEL);
        sendEvent();
        super.onCancel(dialog);
    }

    public void show(
            FragmentManager manager
    ) {
        super.show(manager, getClass().getName());
    }

    public static abstract class BaseOnDialogResultEvent extends BaseFragment.BaseEvent {

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

    public interface DialogResult {
        int COMMIT = 0;
        int NEUTRAL = 1;
        int CANCEL = 2;
    }

}
