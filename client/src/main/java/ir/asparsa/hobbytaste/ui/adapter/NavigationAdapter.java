package ir.asparsa.hobbytaste.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;
import ir.asparsa.hobbytaste.ui.fragment.ContainerFragment;

import javax.inject.Inject;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public class NavigationAdapter extends FragmentPagerAdapter {

    public static final int PAGE_COUNT = 2;

    @Inject
    LanguageUtil mLanguageUtil;

    public NavigationAdapter(FragmentManager fm) {
        super(fm);
        ApplicationLauncher.mainComponent().inject(this);
    }

    @Override
    public Fragment getItem(int pos) {
        return ContainerFragment.instantiate(pos);
    }

    @Override public int getCount() {
        return PAGE_COUNT;
    }

    public int pageToPos(int page) {
        return mLanguageUtil.isRTL() ? PAGE_COUNT - 1 - page : page;
    }
}
