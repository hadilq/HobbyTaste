package ir.asparsa.common.database.model;

/**
 * Created by hadi on 12/30/2016 AD.
 */
public interface StoreLike {
    String TABLE_NAME = "stores_like";

    interface Columns {
        String ID = "id";
        String STORE = "store";
        String ACCOUNT = "account";
    }

}
