package ir.asparsa.common.database.model;

/**
 * Created by hadi on 12/18/2016 AD.
 */
public interface Banner {
    String TABLE_NAME = "banners";

    interface Columns {
        String ID = "id";
        String MAIN_URL = "main_url";
        String THUMBNAIL_URL = "thumbnail_url";
        String MAIN_NAME = "main_name";
        String THUMBNAIL_NAME = "thumbnail_name";
        String STORE = "store";
    }
}
