package ir.asparsa.hobbytaste.core.route;

import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hadi
 * @since 4/25/2017 AD.
 */
public class AnalysedUri {
    private final Uri mUri;
    private final Bundle mBundle;

    private List<String> mLowerCasePathSegments;

    public AnalysedUri(
            Uri uri,
            Bundle bundle
    ) {
        this.mUri = uri;
        this.mBundle = bundle;
    }

    public String getScheme() {
        return mUri.getScheme();
    }

    public String getHost() {
        return mUri.getHost();
    }

    public List<String> getPathSegments() {
        return mUri.getPathSegments();
    }

    public String getQueryParameter(String key) {
        return mUri.getQueryParameter(key);
    }

    public List<String> getLowerCasePathSegments() {
        if (mLowerCasePathSegments == null) {
            mLowerCasePathSegments = new ArrayList<>();
            for (String s : mUri.getPathSegments()) {
                mLowerCasePathSegments.add(s.toLowerCase());
            }
        }
        return mLowerCasePathSegments;
    }

    public Bundle getExtras() {
        return mBundle;
    }
}
