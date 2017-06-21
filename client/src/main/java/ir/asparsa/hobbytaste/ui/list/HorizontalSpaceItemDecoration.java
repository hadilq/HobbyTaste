package ir.asparsa.hobbytaste.ui.list;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;

import javax.inject.Inject;

/**
 * Created by hadi on 1/12/2017 AD.
 */
public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    @Inject
    LanguageUtil mLanguageUtil;

    private final int horizontalSpaceWidth;
    private final int horizontalSpaceEdgeWidth;

    public HorizontalSpaceItemDecoration(
            int horizontalSpaceWidth,
            int horizontalSpaceEdgeWidth
    ) {
        this.horizontalSpaceWidth = horizontalSpaceWidth;
        this.horizontalSpaceEdgeWidth = horizontalSpaceEdgeWidth;
        ApplicationLauncher.mainComponent().inject(this);
    }

    @Override
    public void getItemOffsets(
            Rect outRect,
            View view,
            RecyclerView parent,
            RecyclerView.State state
    ) {
        int position = parent.getChildAdapterPosition(view);
        if (position != parent.getAdapter().getItemCount() - 1) {
            if (mLanguageUtil.isRTL()) {
                outRect.left = horizontalSpaceWidth;
            } else {
                outRect.right = horizontalSpaceWidth;
            }
        } else {
            if (mLanguageUtil.isRTL()) {
                outRect.left = horizontalSpaceEdgeWidth;
            } else {
                outRect.right = horizontalSpaceEdgeWidth;
            }
        }
        if (position == 0) {
            if (mLanguageUtil.isRTL()) {
                outRect.right = horizontalSpaceEdgeWidth;
            } else {
                outRect.left = horizontalSpaceEdgeWidth;
            }
        }
    }
}