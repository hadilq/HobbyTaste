package ir.asparsa.hobbytaste.ui.list.data;

import android.os.Parcel;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.hobbytaste.R;

/**
 * Created by hadi on 1/13/2017 AD.
 */
public class LanguageData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.language;

    private String mLanguage;
    private String mLangAbbreviation;

    public LanguageData(
            String language,
            String langAbbreviation
    ) {
        this.mLanguage = language;
        this.mLangAbbreviation = langAbbreviation;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        this.mLanguage = language;
    }

    public String getLangAbbreviation() {
        return mLangAbbreviation;
    }

    public void setLangAbbreviation(String mLangAbbreviation) {
        this.mLangAbbreviation = mLangAbbreviation;
    }

    @Override public int getViewType() {
        return VIEW_TYPE;
    }

    @Override public boolean equals(Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof LanguageData)) {
            return false;
        }
        final LanguageData other = (LanguageData) otherObj;
        return (getLangAbbreviation() == null && other.getLangAbbreviation() == null) ||
               (getLangAbbreviation() != null && getLangAbbreviation().equals(other.getLangAbbreviation()));
    }


    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(
            Parcel dest,
            int flags
    ) {
        dest.writeString(this.mLanguage);
        dest.writeString(this.mLangAbbreviation);
    }

    protected LanguageData(Parcel in) {
        this.mLanguage = in.readString();
        this.mLangAbbreviation = in.readString();
    }

    public static final Creator<LanguageData> CREATOR = new Creator<LanguageData>() {
        @Override public LanguageData createFromParcel(Parcel source) {
            return new LanguageData(source);
        }

        @Override public LanguageData[] newArray(int size) {
            return new LanguageData[size];
        }
    };
}
