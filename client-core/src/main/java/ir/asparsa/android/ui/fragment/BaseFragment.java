package ir.asparsa.android.ui.fragment;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public abstract class BaseFragment extends Fragment {

    public String getTagName() {
        return getClass().getName();
    }

    public void onEvent(BaseEvent event) {
    }

    public static abstract class BaseEvent implements Parcelable {
        private String mSourceTag;

        public BaseEvent(@NonNull String sourceTag) {
            this.mSourceTag = sourceTag;
        }

        public String getSourceTag() {
            return mSourceTag;
        }


        @Override public int describeContents() {
            return 0;
        }

        @Override public void writeToParcel(
                Parcel dest,
                int flags
        ) {
            dest.writeString(this.mSourceTag);
        }

        protected BaseEvent(Parcel in) {
            this.mSourceTag = in.readString();
        }
    }

}
