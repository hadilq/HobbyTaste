package ir.asparsa.common.database.model;

/**
 * @author hadi
 * @since 3/10/2017 AD.
 */
public interface Feedback {
    String TABLE_NAME = "feedback";

    interface Columns {
        String DATETIME = "datetime";
        String BODY = "body";
        String THROWABLE = "throwable";
        String MESSAGE = "message";
        String ACCOUNT = "account";
    }

}
