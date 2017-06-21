package ir.asparsa.hobbytaste.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.route.MainRoute;
import ir.asparsa.hobbytaste.core.route.RouteFactory;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;
import ir.asparsa.hobbytaste.core.util.LaunchUtil;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.core.util.UncaughtExceptionHandler;
import ir.asparsa.hobbytaste.ui.adapter.NavigationAdapter;
import ir.asparsa.hobbytaste.ui.behavior.ShrinkBehavior;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class LaunchActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener {

    public static final String BUNDLE_KEY_CONFIGURATION_CHANGED = "BUNDLE_KEY_CONFIGURATION_CHANGED";

    @Inject
    RouteFactory mRouteFactory;
    @Inject
    NavigationUtil mNavigationUtil;
    @Inject
    LanguageUtil mLanguageUtil;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;

    private List<Integer> actionList = new ArrayList<Integer>() {{
        add(R.id.action_main);
        add(R.id.action_settings);
    }};
    private NavigationAdapter mPagerAdapter;
    private boolean mConfigurationChanged = false;
    private ShrinkBehavior mShrinkBehavior;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_activity);
        ApplicationLauncher.mainComponent().inject(this);

        ButterKnife.bind(this);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title));
        setSupportActionBar(mToolbar);
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mRouteFactory.pageToPos(MainRoute.PAGE));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(
                    int position,
                    float positionOffset,
                    int positionOffsetPixels
            ) {
            }

            @Override public void onPageSelected(int position) {
                onBackStackChanged();
            }

            @Override public void onPageScrollStateChanged(int state) {
            }
        });

        mBottomNavigationView
                .setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        for (Integer action : actionList) {
                            if (action == item.getItemId()) {
                                mViewPager.setCurrentItem(mRouteFactory.pageToPos(actionList.indexOf(action)));
                            }
                        }
                        return true;
                    }
                });

        mShrinkBehavior = (ShrinkBehavior) ((CoordinatorLayout.LayoutParams) mFloatingActionButton.getLayoutParams())
                .getBehavior();

        if (UncaughtExceptionHandler.DEBUG) {
            throw new RuntimeException("Test");
        }

        Intent intent = getIntent();
        boolean handled = mRouteFactory.handleIntent(getResources(), intent, mViewPager);
        if (!handled) {
            if (!intent.hasExtra(BUNDLE_KEY_CONFIGURATION_CHANGED)) {
                mConfigurationChanged = false;
                LaunchUtil.launch(this, SplashActivity.class);
            } else {
                mConfigurationChanged = true;
                intent.removeExtra(BUNDLE_KEY_CONFIGURATION_CHANGED);
            }
        }
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mRouteFactory.handleIntent(getResources(), intent, mViewPager);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getIntent().putExtra(BUNDLE_KEY_CONFIGURATION_CHANGED, newConfig.orientation);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override public void onBackPressed() {
        int pos = mViewPager.getCurrentItem();
        FragmentManager fragmentManager = mRouteFactory.getFragmentManager(pos);
        if (fragmentManager == null) {
            return;
        }
        BaseContentFragment fragment = mNavigationUtil.findTopFragment(fragmentManager);
        if (fragment != null) {
            BaseContentFragment.BackState state = fragment.onBackPressed();
            switch (state) {
                case CLOSE_APP:
                    finish();
                    break;
                case BACK_FRAGMENT:
                    List<Fragment> fragments = fragmentManager.getFragments();
                    mNavigationUtil.popBackStack(fragmentManager);
                    L.i(getClass(), "Fragments: " + fragments);
                    if (fragments == null || fragments.size() <= 1) {
                        mRouteFactory.launchRoute(getResources(), pos);
                    }
                    break;
            }
        }
    }

    @Override protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager = mRouteFactory.getFragmentManager(mViewPager.getCurrentItem());
        if (fragmentManager == null) {
            return;
        }
        BaseContentFragment fragment = mNavigationUtil.findTopFragment(fragmentManager);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override public void onBackStackChanged() {
        FragmentManager fragmentManager = mRouteFactory.getFragmentManager(mViewPager.getCurrentItem());
        if (fragmentManager == null) {
            return;
        }
        BaseContentFragment fragment = mNavigationUtil.findTopFragment(fragmentManager);
        if (fragment != null) {
            mToolbar.setTitle(fragment.getHeaderTitle());

            final BaseContentFragment.FloatingActionButtonObserver fabObserver = fragment
                    .getFloatingActionButtonObserver();
            if (fabObserver != null) {
                mFloatingActionButton.show();
                mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        fabObserver.onNext(v);
                    }
                });
                mShrinkBehavior.setShowFloatingActionButton(true);
            } else {
                mFloatingActionButton.hide();
                mFloatingActionButton.setOnClickListener(null);
                mShrinkBehavior.setShowFloatingActionButton(false);
            }

            boolean scrollToolbar = fragment.scrollToolbar();
            mShrinkBehavior.setScrollToolbar(scrollToolbar);
            AppBarLayout.LayoutParams tp =
                    (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
            if (scrollToolbar) {
                tp.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                                  AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS |
                                  AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
                );
            } else {
                tp.setScrollFlags(0);
            }

            if (fragment.hasHomeAsUp()) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                if (mLanguageUtil.isRTL()) {
                    mToolbar.setNavigationIcon(R.drawable.ic_rtl_arrow);
                } else {
                    mToolbar.setNavigationIcon(R.drawable.ic_ltr_arrow);
                }
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
            }
        }
    }

    public boolean isConfigurationChanged() {
        return mConfigurationChanged;
    }
}
