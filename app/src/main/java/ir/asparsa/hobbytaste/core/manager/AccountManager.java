package ir.asparsa.hobbytaste.core.manager;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by hadi on 12/14/2016 AD.
 */
@Singleton
public class AccountManager {

    @Inject
    PreferencesManager mSharedPreferencesManager;

    private String username;

    @Inject
    public AccountManager() {
    }

    public String getUsername() {
        if (TextUtils.isEmpty(username)) {
            username = mSharedPreferencesManager.getString(PreferencesManager.KEY_USERNAME, "");
        }
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
        mSharedPreferencesManager.put(PreferencesManager.KEY_USERNAME, username);
    }
}
