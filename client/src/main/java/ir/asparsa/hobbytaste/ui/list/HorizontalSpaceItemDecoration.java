package ir.asparsa.hobbytaste.ui.list;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;

/**
 * Created by hadi on 1/12/2017 AD.
 */
public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;

    public HorizontalSpaceItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(
            Rect outRect,
            View view,
            RecyclerView parent,
            RecyclerView.State state
    ) {
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            if (LanguageUtil.isRTL()) {
                outRect.left = verticalSpaceHeight;
            } else {
                outRect.right = verticalSpaceHeight;
            }
        }
    }
}