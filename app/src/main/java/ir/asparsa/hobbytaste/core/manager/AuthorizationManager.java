package ir.asparsa.hobbytaste.core.manager;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
@Singleton
public class AuthorizationManager {

    private final Object tokenSync = new Object();
    private final Object usernameSync = new Object();

    private String token;
    private String username;

    @Inject
    PreferencesManager mPreferencesManager;

    @Inject
    public AuthorizationManager() {
    }

    public String getToken() {
        loadToken();
        return token;
    }

    public void setToken(String token) {
        synchronized (tokenSync) {
            this.token = token;
            mPreferencesManager.put(PreferencesManager.KEY_AUTHORIZATION_TOKEN, token);
        }
    }

    public boolean isAuthenticated() {
        loadToken();
        return !TextUtils.isEmpty(token);
    }

    public String getUsername() {
        loadUsername();
        return username;
    }

    public void setUsername(@NonNull String username) {
        synchronized (usernameSync) {
            this.username = username;
            mPreferencesManager.put(PreferencesManager.KEY_USERNAME, username);
        }
    }

    private void loadToken() {
        if (TextUtils.isEmpty(token)) {
            synchronized (tokenSync) {
                if (TextUtils.isEmpty(token)) {
                    token = mPreferencesManager.getString(PreferencesManager.KEY_AUTHORIZATION_TOKEN, "");
                }
            }
        }
    }

    private void loadUsername() {
        if (TextUtils.isEmpty(username)) {
            synchronized (usernameSync) {
                if (TextUtils.isEmpty(username)) {
                    username = mPreferencesManager.getString(PreferencesManager.KEY_USERNAME, "");
                }
            }
        }
    }

}
