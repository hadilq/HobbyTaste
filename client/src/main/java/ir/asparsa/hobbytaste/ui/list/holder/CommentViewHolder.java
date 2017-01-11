package ir.asparsa.hobbytaste.ui.list.holder;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
    @BindView(R.id.heart)
    ImageView mHeartImageView;
    @BindView(R.id.datetime)
    TextView mDateTimeTextView;
    @BindView(R.id.rate)
    TextView mRateTextView;

    public CommentViewHolder(
            View itemView,
            Observer<BaseRecyclerFragment.Event> observer,
            Bundle savedInstanceState
    ) {
        super(itemView, observer, savedInstanceState);
        ButterKnife.bind(this, itemView);
    }

    @Override public void onBindView(final CommentData data) {
        mCommentTextView.setText(data.getCommentModel().getDescription());

        mRateTextView.setText(itemView.getContext().getResources()
                                      .getString(R.string.rating, data.getCommentModel().getRate(),
                                                 itemView.getContext().getResources().getQuantityString(
                                                         R.plurals.like, (int) data.getCommentModel().getRate())));

        mDateTimeTextView.setText(Long.toString(data.getCommentModel().getCreated()));

        mHeartImageView.getDrawable().setColorFilter(
                itemView.getResources()
                        .getColor(data.getCommentModel().isLiked() ? R.color.heart_on : R.color.heart_off),
                PorterDuff.Mode.SRC_ATOP);
        mHeartImageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (mObserver != null) {
                    mObserver.onNext(new OnHeartClick(data));
                }
            }
        });
    }

    public static class OnHeartClick implements BaseRecyclerFragment.Event {
        private CommentData  comment;

        OnHeartClick(CommentData comment) {
            this.comment = comment;
        }

        public CommentData getComment() {
            return comment;
        }
    }
}
