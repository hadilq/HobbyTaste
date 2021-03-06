package ir.asparsa.hobbytaste.ui.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author hadi
 * @since 1/12/2017 AD.
 */
public class ShrinkBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    private boolean mScrollToolbar;
    private boolean mShowFloatingActionButton;

    public ShrinkBehavior(
            Context context,
            AttributeSet attrs
    ) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(
            final CoordinatorLayout coordinatorLayout,
            final FloatingActionButton child,
            final View directTargetChild,
            final View target,
            final int nestedScrollAxes
    ) {
        return mScrollToolbar;
    }

    @Override
    public void onNestedScroll(
            final CoordinatorLayout coordinatorLayout,
            final FloatingActionButton child,
            final View target,
            final int dxConsumed,
            final int dyConsumed,
            final int dxUnconsumed,
            final int dyUnconsumed
    ) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (!mShowFloatingActionButton) {
            child.hide();
            return;
        }
        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.hide();
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.show();
        }
    }

    public void setScrollToolbar(boolean scrollToolbar) {
        this.mScrollToolbar = scrollToolbar;
    }

    public void setShowFloatingActionButton(boolean showFloatingActionButton) {
        this.mShowFloatingActionButton = showFloatingActionButton;
    }
}