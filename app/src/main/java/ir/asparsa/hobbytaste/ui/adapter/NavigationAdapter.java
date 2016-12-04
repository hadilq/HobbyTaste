package ir.asparsa.hobbytaste.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;
import ir.asparsa.hobbytaste.ui.fragment.container.MainContainerFragment;
import ir.asparsa.hobbytaste.ui.fragment.container.SettingContainerFragment;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public class NavigationAdapter extends FragmentPagerAdapter {

    public static final int PAGE_MAIN = 0;
    public static final int PAGE_SETTINGS = 1;
    public static final int POS_FIRST = 0;
    public static final int POS_SECOND = 1;
    public static final int PAGE_COUNT = 2;

    private final int[] mPosToPag = new int[PAGE_COUNT];

    private final FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener;

    public NavigationAdapter(
            FragmentManager fm, FragmentManager.OnBackStackChangedListener onBackStackChangedListener) {
        super(fm);
        this.mOnBackStackChangedListener = onBackStackChangedListener;

        if (LanguageUtil.isRTL()) {
            mPosToPag[POS_FIRST] = PAGE_SETTINGS;
            mPosToPag[POS_SECOND] = PAGE_MAIN;
        } else {
            mPosToPag[POS_FIRST] = PAGE_MAIN;
            mPosToPag[POS_SECOND] = PAGE_SETTINGS;
        }
    }

    @Override
    public Fragment getItem(int pos) {
        int page = posToPage(pos);
        switch (page) {
            case PAGE_MAIN:
                return MainContainerFragment.instantiate(pos);
            case PAGE_SETTINGS:
                return SettingContainerFragment.instantiate(pos);
        }
        return null;
    }

    @Override public int getCount() {
        return PAGE_COUNT;
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    public int posToPage(int pos) {
        return mPosToPag[pos];
    }

    public int pageToPos(int page) {
        for (int pos = 0; pos < mPosToPag.length; pos++) {
            if (mPosToPag[pos] == page) {
                return pos;
            }
        }
        return POS_FIRST;
    }
}
