package ir.asparsa.hobbytaste.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;

import javax.inject.Inject;

/**
 * @author hadi
 */
public class BaseActivity extends AppCompatActivity {


    @Inject
    PreferencesManager mPreferencesManager;
    @Inject
    LanguageUtil mLanguageUtil;

    @Override protected void onCreate(
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);
        mLanguageUtil.setupDefaultLocale(mPreferencesManager, this);
    }
}
