package ir.asparsa.hobbytaste.core.manager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by hadi on 12/14/2016 AD.
 */
@Singleton
public class AccountManager {

    @Inject
    PreferencesManager mSharedPreferencesManager;

    @Inject
    public AccountManager() {
    }


}
