package ir.asparsa.hobbytaste.ui.fragment.dialog;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.ui.fragment.dialog.BaseDialogFragment;
import ir.asparsa.android.ui.view.DialogControlLayout;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;

/**
 * Created by hadi on 12/15/2016 AD.
 */
public class LanguageDialogFragment extends BaseDialogFragment {

    public static final String BUNDLE_KEY_LANGUAGE = "BUNDLE_KEY_STORE";

    @BindView(R.id.title)
    TextView mTitleTextView;
    @BindView(R.id.language_radio_group)
    RadioGroup mLanguageRadioGroup;
    @BindView(R.id.controller)
    DialogControlLayout mController;

    public static LanguageDialogFragment instantiate(
            @NonNull String language,
            @NonNull OnChangeLanguageDialogResultEvent event
    ) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_LANGUAGE, language);
        LanguageDialogFragment fragment = new LanguageDialogFragment();
        fragment.setArguments(bundle);
        fragment.setOnDialogResultEvent(event);
        return fragment;
    }

    @Nullable @Override public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_set_language, container, false);
        ButterKnife.bind(this, view);

        String language = getArguments().getString(BUNDLE_KEY_LANGUAGE);
        mLanguageRadioGroup.check(LanguageUtil.LANGUAGE_EN.equals(language) ? R.id.english_radio_button :
                                  R.id.persian_radio_button);

        mController.setCommitText(getString(R.string.commit))
                   .arrange();
        mController.setOnControlListener(new DialogControlLayout.OnControlListener() {
            @Override public void onCommit() {
                actionChangeLanguage(mLanguageRadioGroup.getCheckedRadioButtonId());
            }

            @Override public void onNeutral() {
            }

            @Override public void onCancel() {
            }
        });

        return view;
    }

    private void actionChangeLanguage(int selectedId) {
        OnChangeLanguageDialogResultEvent event
                = (OnChangeLanguageDialogResultEvent) getOnDialogResultEvent();
        event.setLanguage(
                selectedId == R.id.english_radio_button ? LanguageUtil.LANGUAGE_EN : LanguageUtil.LANGUAGE_FA);
        event.setDialogResult(DialogResult.COMMIT);
        sendEvent();
        dismiss();
    }

    public static class OnChangeLanguageDialogResultEvent extends BaseOnDialogResultEvent {

        @LanguageUtil.Language
        private String language;

        public OnChangeLanguageDialogResultEvent(@NonNull String sourceTag) {
            super(sourceTag);
        }

        @LanguageUtil.Language
        public String getLanguage() {
            return language;
        }

        public void setLanguage(@LanguageUtil.Language String language) {
            this.language = language;
        }

        @Override public int describeContents() {
            return 0;
        }

        @Override public void writeToParcel(
                Parcel dest,
                int flags
        ) {
            super.writeToParcel(dest, flags);
            dest.writeString(this.language);
        }

        protected OnChangeLanguageDialogResultEvent(Parcel in) {
            super(in);
            this.language = LanguageUtil.LANGUAGE_EN.equals(in.readString()) ? LanguageUtil.LANGUAGE_EN :
                            LanguageUtil.LANGUAGE_FA;
        }

        public static final Creator<OnChangeLanguageDialogResultEvent> CREATOR
                = new Creator<OnChangeLanguageDialogResultEvent>() {
            @Override public OnChangeLanguageDialogResultEvent createFromParcel(Parcel source) {
                return new OnChangeLanguageDialogResultEvent(source);
            }

            @Override public OnChangeLanguageDialogResultEvent[] newArray(int size) {
                return new OnChangeLanguageDialogResultEvent[size];
            }
        };
    }

}
