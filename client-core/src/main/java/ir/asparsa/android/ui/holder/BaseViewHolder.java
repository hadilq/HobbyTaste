package ir.asparsa.android.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import ir.asparsa.android.ui.data.BaseRecyclerData;

/**
 * @author hadi
 * @since 6/24/2016 AD
 */
public abstract class BaseViewHolder<DATA extends BaseRecyclerData> extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBindView(DATA data);
}
