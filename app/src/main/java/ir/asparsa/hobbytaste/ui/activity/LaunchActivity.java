package ir.asparsa.hobbytaste.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.LaunchUtil;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.ui.adapter.NavigationAdapter;
import ir.asparsa.hobbytaste.ui.fragment.container.BaseContainerFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hadi
 * @since 6/23/2016 AD
 */
public class LaunchActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private final BaseContainerFragment[] mContainers = new BaseContainerFragment[NavigationAdapter.PAGE_COUNT];

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private List<Integer> actionList = new ArrayList<Integer>() {{
        add(R.id.action_main);
        add(R.id.action_settings);
    }};
    private NavigationAdapter mPagerAdapter;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_activity);
        ApplicationLauncher.mainComponent().inject(this);

        ButterKnife.bind(this);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title));
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mPagerAdapter.pageToPos(NavigationAdapter.PAGE_MAIN));
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                return true;
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
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
                                mViewPager.setCurrentItem(mPagerAdapter.pageToPos(actionList.indexOf(action)));
                            }
                        }
                        return true;
                    }
                });


        LaunchUtil.launch(this, SplashActivity.class);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }

    @Override public void onBackPressed() {
        FragmentManager activeFragmentManager = getActiveFragmentManager();
        BaseContentFragment fragment = NavigationUtil.findTopFragment(activeFragmentManager, true);
        if (fragment != null) {
            BaseContentFragment.BackState state = fragment.onBackPressed();
            switch (state) {
                case CLOSE_APP:
                    finish();
                    break;
                case BACK_FRAGMENT:
                    NavigationUtil.popBackStack(activeFragmentManager);
                    break;
            }
        }
    }

    @Override public void onBackStackChanged() {
        BaseContentFragment fragment = NavigationUtil.findTopFragment(getActiveFragmentManager(), true);
        if (fragment != null) {
            mToolbar.setTitle(fragment.getHeaderTitle());
        }
    }

    private FragmentManager getActiveFragmentManager() {
        return mContainers[mViewPager.getCurrentItem()].getChildFragmentManager();
    }

    public void addContainer(@NonNull BaseContainerFragment fragment, int pos) {
        mContainers[pos] = fragment;
        fragment.getChildFragmentManager().addOnBackStackChangedListener(this);
    }
}
