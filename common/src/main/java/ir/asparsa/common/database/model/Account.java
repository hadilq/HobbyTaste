package ir.asparsa.common.database.model;

/**
 * Created by hadi on 12/18/2016 AD.
 */
public interface Account {
    String TABLE_NAME = "accounts";

    interface Columns {
        String PASSWORD = "password";
        String USERNAME = "username";
        String HASH_CODE = "hash_code";
        String CREATED = "created";
        String ROLE = "role";
    }
}
