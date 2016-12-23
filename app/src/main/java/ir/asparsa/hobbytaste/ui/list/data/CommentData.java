package ir.asparsa.hobbytaste.ui.list.data;

import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.database.model.BannerModel;
import ir.asparsa.hobbytaste.database.model.CommentModel;

/**
 * Created by hadi on 12/22/2016 AD.
 */
public class CommentData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.comment;

    private CommentModel mCommentModel;

    public CommentData(CommentModel commentModel) {
        this.mCommentModel = commentModel;
    }

    public CommentModel getCommentModel() {
        return mCommentModel;
    }

    @Override public int getViewType() {
        return VIEW_TYPE;
    }

    @Override public boolean equals(Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof CommentData)) {
            return false;
        }
        final CommentData other = (CommentData) otherObj;
        return (getCommentModel() == null && other.getCommentModel() == null) ||
               (getCommentModel() != null && getCommentModel().equals(other.getCommentModel()));
    }
}
