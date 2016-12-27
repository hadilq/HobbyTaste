package ir.asparsa.hobbytaste.core.dagger;

import dagger.Module;
import ir.asparsa.hobbytaste.database.DatabaseHelper;
import ir.asparsa.hobbytaste.database.dao.StoreDao;

import javax.inject.Inject;

/**
 * @author hadi
 * @since 11/30/2016 AD
 */
@Module
public class DatabaseModule {

    @Inject
    DatabaseHelper databaseHelper;
    @Inject
    StoreDao storeDao;
}
