package ir.asparsa.common.database.model;

/**
 * Created by hadi on 12/30/2016 AD.
 */
public interface CommentLike {
    String TABLE_NAME = "comments_like";

    interface Columns {
        String ID = "id";
        String COMMENT = "comment";
        String ACCOUNT = "account";
    }

}
