package ir.asparsa.hobbytaste.database.dao;

import ir.asparsa.hobbytaste.database.DatabaseHelper;
import ir.asparsa.hobbytaste.database.model.BannerModel;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@Singleton
public class BannerDao extends AbsDao<BannerModel, Long> {

    @Inject
    public BannerDao(DatabaseHelper mDatabaseHelper) {
        super(mDatabaseHelper);
    }

    @Override protected Class<BannerModel> getModel() {
        return BannerModel.class;
    }
}
