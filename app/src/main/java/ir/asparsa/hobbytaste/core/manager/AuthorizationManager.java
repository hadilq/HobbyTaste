package ir.asparsa.hobbytaste.core.manager;

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

    private String token;

    @Inject
    PreferenceManager mPreferenceManager;

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
            mPreferenceManager.put(PreferenceManager.PREFERENCES_KEY_AUTHENTICATION, token);
        }
    }

    public boolean isAuthenticated() {
        loadToken();
        return !TextUtils.isEmpty(token);
    }

    private void loadToken() {
        if (TextUtils.isEmpty(token)) {
            synchronized (tokenSync) {
                if (TextUtils.isEmpty(token)) {
                    token = mPreferenceManager.get(PreferenceManager.PREFERENCES_KEY_AUTHENTICATION, "");
                }
            }
        }
    }
}
