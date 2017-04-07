package ir.asparsa.common.database.model;

/**
 * Created by hadi on 12/18/2016 AD.
 */
public interface Comment {
    String TABLE_NAME = "comments";

    interface Columns {
        String ID = "id";
        String DESCRIPTION = "description";
        String RATE = "rate";
        String CREATED = "created";
        String CREATOR = "creator";
        String HASH_CODE = "hash_code";
        String STORE = "store";
        String LIKE = "like";
    }

}
