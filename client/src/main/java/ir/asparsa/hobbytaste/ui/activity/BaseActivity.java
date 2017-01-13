package ir.asparsa.hobbytaste.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;

/**
 * Created by hadi on 1/13/2017 AD.
 */
public class BaseActivity extends AppCompatActivity {


    @Override public void onCreate(
            Bundle savedInstanceState,
            PersistableBundle persistentState
    ) {
        super.onCreate(savedInstanceState, persistentState);
        LanguageUtil.setupDefaultLocale(this);
    }
}
