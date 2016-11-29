package ir.asparsa.hobbytaste.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.LaunchUtil;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.MainContentFragment;
import junit.framework.Assert;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class LaunchActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_activity);
        ApplicationLauncher.mainComponent().inject(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        NavigationUtil.startFragment(this, MainContentFragment.instantiate());

        ButterKnife.bind(this);

        LaunchUtil.launch(this, SplashActivity.class);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }

    @Override public void onBackPressed() {
        Fragment fragment = NavigationUtil.findTopFragment(this);
        if (fragment instanceof BaseContentFragment) {
            BaseContentFragment baseContentFragment = (BaseContentFragment) fragment;
            BaseContentFragment.BackState state = baseContentFragment.onBackPressed();
            switch (state) {
                case CLOSE_APP:
                    finish();
                    break;
                case BACK_FRAGMENT:
                    NavigationUtil.popBackStack(this);
                    break;
            }
        } else {
            Assert.fail("All fragments in Launch Activity must be a Base Content Fragment");
        }
    }


    @Override public void onBackStackChanged() {
        Fragment fragment = NavigationUtil.findTopFragment(this);
        if (fragment instanceof BaseContentFragment) {
            BaseContentFragment baseContentFragment = (BaseContentFragment) fragment;
            mToolbar.setTitle(baseContentFragment.getHeaderTitle());
        } else {
            Assert.fail("All fragments must be Base Content Fragment");
        }

    }
}
