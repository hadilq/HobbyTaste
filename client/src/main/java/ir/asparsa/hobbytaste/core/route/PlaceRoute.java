package ir.asparsa.hobbytaste.core.route;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.database.dao.StoreDao;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.fragment.content.BaseContentFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.StoreDetailsContentFragment;
import junit.framework.Assert;
import rx.Observer;

import javax.inject.Inject;

/**
 * @author hadi
 * @since 4/28/2017 AD.
 */
public class PlaceRoute implements Route {

    public static final int PAGE = 0;

    @Inject
    StoreDao mStoreDao;

    private final String mSegment;
    private StoreModel mStoreModel;

    PlaceRoute(Resources resources) {
        ApplicationLauncher.mainComponent().inject(this);
        mSegment = resources.getString(R.string.path_segment_place);
    }

    @Override public boolean shouldFire(AnalysedUri uri) {
        return uri.getLowerCasePathSegments().size() > 1 && uri.getLowerCasePathSegments().get(0).equals(mSegment);
    }

    @Nullable @Override public Route fire(AnalysedUri uri) {
        mStoreModel = null;
        long storeHashCode;
        try {
            storeHashCode = Long.parseLong(uri.getPathSegments().get(1));
        } catch (NumberFormatException e) {
            return null;
        }
        mStoreDao.findByHashCode(storeHashCode)
                 .toBlocking()
                 .subscribe(new Observer<StoreModel>() {
                     @Override public void onCompleted() {
                     }

                     @Override public void onError(Throwable e) {
                         L.w(PlaceRoute.class, "cannot retrieve store", e);
                     }

                     @Override public void onNext(@Nullable StoreModel storeModel) {
                         mStoreModel = storeModel;
                     }
                 });
        if (mStoreModel == null) {
            return null;
        }
        return this;
    }

    @Override public int whichPage() {
        return PAGE;
    }

    @NonNull @Override public BaseContentFragment getFragment() {
        Assert.assertNotNull(mStoreModel);
        return StoreDetailsContentFragment.instantiate(mStoreModel);
    }

    @Override public boolean isAlwaysBellow() {
        return false;
    }
}
