package ir.asparsa.hobbytaste.ui.list.holder;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.list.data.CommentData;
import rx.Observer;

/**
 * Created by hadi on 12/22/2016 AD.
 */
public class CommentViewHolder extends BaseViewHolder<CommentData> {

    @BindView(R.id.comment)
    TextView mCommentTextView;

    public CommentViewHolder(
            View itemView,
            Observer<BaseRecyclerFragment.Event> observer,
            Bundle savedInstanceState
    ) {
        super(itemView, observer, savedInstanceState);
        ButterKnife.bind(this, itemView);
    }

    @Override public void onBindView(CommentData data) {
        mCommentTextView.setText(data.getCommentModel().getDescription());
    }
}
