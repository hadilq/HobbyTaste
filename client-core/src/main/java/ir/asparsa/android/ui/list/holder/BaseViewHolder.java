package ir.asparsa.android.ui.list.holder;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;

/**
 * @author hadi
 */
public abstract class BaseViewHolder<DATA extends BaseRecyclerData> extends RecyclerView.ViewHolder {

    private DATA mData;

    public BaseViewHolder(
            View itemView
    ) {
        super(itemView);
    }

    public void onBindView(DATA data) {
        mData = data;
    }

    public void onCreate(Bundle savedInstanceState) {
    }

    public DATA getData() {
        return mData;
    }

    public void onResume() {
    }

    public void onStart() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onDestroy() {
    }

    public void onLowMemory() {
    }
}
