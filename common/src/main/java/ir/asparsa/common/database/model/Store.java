package ir.asparsa.common.database.model;

/**
 * Created by hadi on 12/18/2016 AD.
 */
public interface Store {
    String TABLE_NAME = "stores";

    interface Columns {
        String ID = "id";
        String LAT = "lat";
        String LON = "lon";
        String TITLE = "title";
        String DESCRIPTION = "description";
        String HASH_CODE = "hashCode";
        String CREATED = "created";
        String VIEWED = "viewed";
        String RATE = "rate";
        String BANNERS = "banners";
        String LIKE = "like";

    }
}
