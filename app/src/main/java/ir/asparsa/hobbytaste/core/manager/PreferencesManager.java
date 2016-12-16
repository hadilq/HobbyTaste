package ir.asparsa.hobbytaste.core.manager;

import android.content.Context;
import ir.asparsa.android.core.manager.CorePreferencesManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by hadi on 12/14/2016 AD.
 */
@Singleton
public class PreferencesManager extends CorePreferencesManager {

    public static final String KEY_USERNAME = getNotVersionedKey("KEY_USERNAME");
    public static final String KEY_AUTHORIZATION_TOKEN = getNotVersionedKey("KEY_AUTHORIZATION_TOKEN");

    @Inject
    public PreferencesManager(Context context) {
        super(context);
    }
}
