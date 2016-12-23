package ir.asparsa.hobbytaste.ui.list.holder;

import android.os.Bundle;
import android.view.View;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.ui.list.data.CommentData;
import rx.Observer;

/**
 * Created by hadi on 12/22/2016 AD.
 */
public class CommentViewHolder extends BaseViewHolder<CommentData> {

    public CommentViewHolder(
            View itemView,
            Observer observer,
            Bundle savedInstanceState
    ) {
        super(itemView, observer, savedInstanceState);
    }

    @Override public void onBindView(CommentData data) {

    }
}
