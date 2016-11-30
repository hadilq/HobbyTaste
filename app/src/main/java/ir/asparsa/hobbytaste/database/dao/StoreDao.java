package ir.asparsa.hobbytaste.database.dao;

import ir.asparsa.hobbytaste.database.DatabaseHelper;
import ir.asparsa.hobbytaste.database.model.StoreModel;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@Singleton
public class StoreDao extends AbsDao<StoreModel, Integer> {

    @Inject
    public StoreDao(DatabaseHelper mDatabaseHelper) {
        super(mDatabaseHelper);
    }

    @Override protected Class<StoreModel> getModel() {
        return StoreModel.class;
    }
}
