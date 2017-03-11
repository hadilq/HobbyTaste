package ir.asparsa.common.database.model;

/**
 * @author hadi
 * @since 3/10/2017 AD.
 */
public interface RequestLog {
    String TABLE_NAME = "request_like";

    interface Columns {
        String DATETIME = "datetime";
        String ADDRESS = "address";
        String URI = "uri";
        String SESSION_ID = "session_id";
        String ACCOUNT = "account";
    }

}
