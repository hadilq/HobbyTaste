package ir.asparsa.android.ui.list.holder;

import android.os.Bundle;
import android.view.View;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;

/**
 * @author hadi
 * @since 6/24/2016 AD
 */
public class EmptyViewHolder extends BaseViewHolder {

    public EmptyViewHolder(View itemView, Bundle savedInstanceState) {
        super(itemView, savedInstanceState);
    }

    @Override
    public void onBindView(BaseRecyclerData data) {

    }

}
