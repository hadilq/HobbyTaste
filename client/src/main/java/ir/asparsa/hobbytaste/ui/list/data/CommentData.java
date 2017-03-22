package ir.asparsa.hobbytaste.ui.list.data;

import android.os.Parcel;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.database.model.CommentModel;

/**
 * @author hadi
 * @since 12/22/2016 AD.
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

    public void setCommentModel(CommentModel commentModel) {
        this.mCommentModel = commentModel;
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

    public void heartBeat() {
        getCommentModel().heartBeat();
    }


    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(
            Parcel dest,
            int flags
    ) {
        dest.writeParcelable(this.mCommentModel, flags);
    }

    protected CommentData(Parcel in) {
        this.mCommentModel = in.readParcelable(CommentModel.class.getClassLoader());
    }

    public static final Creator<CommentData> CREATOR = new Creator<CommentData>() {
        @Override public CommentData createFromParcel(Parcel source) {
            return new CommentData(source);
        }

        @Override public CommentData[] newArray(int size) {
            return new CommentData[size];
        }
    };
}
