package ir.asparsa.hobbytaste.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntDef;
import android.support.v4.util.SparseArrayCompat;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.core.model.BaseModel;
import ir.asparsa.hobbytaste.database.model.BannerModel;
import ir.asparsa.hobbytaste.database.model.CommentModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.SQLException;

/**
 * @author hadi
 * @since 6/25/2016 AD
 */

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
@Singleton
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "hobby.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 8;

    @IntDef({Models.STORE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TableModel {
    }

    public interface Models {
        int STORE = 0;
        int BANNER = 1;
        int COMMENT = 2;
    }

    public static final SparseArrayCompat<Class<? extends BaseModel>> MODELS_LIST =
            new SparseArrayCompat<Class<? extends BaseModel>>() {{
                put(Models.STORE, StoreModel.class);
                put(Models.BANNER, BannerModel.class);
                put(Models.COMMENT, CommentModel.class);
            }};

    @Inject
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(
            SQLiteDatabase db,
            ConnectionSource connectionSource
    ) {
        try {
            L.i(DatabaseHelper.class, "onCreate");
            for (int i = 0; i < MODELS_LIST.size(); i++) {
                Class<? extends BaseModel> clazz = MODELS_LIST.get(MODELS_LIST.keyAt(i));
                TableUtils.createTable(connectionSource, clazz);
            }
        } catch (SQLException e) {
            L.e(DatabaseHelper.class, "Can't create database", e);
            throw new RuntimeException(e);
        }

    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            ConnectionSource connectionSource,
            int oldVersion,
            int newVersion
    ) {
        try {
            L.i(DatabaseHelper.class, "onUpgrade");
            for (int i = 0; i < MODELS_LIST.size(); i++) {
                Class<? extends BaseModel> clazz = MODELS_LIST.get(MODELS_LIST.keyAt(i));
                TableUtils.dropTable(connectionSource, clazz, true);
            }
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            L.e(DatabaseHelper.class, "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
    }
}