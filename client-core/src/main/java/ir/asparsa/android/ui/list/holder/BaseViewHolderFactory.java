package ir.asparsa.android.ui.list.holder;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * @author hadi
 */
public interface BaseViewHolderFactory {

    @NonNull
    BaseViewHolder create(
            @NonNull Class<? extends BaseViewHolder> clazz,
            @NonNull View itemView
    );
}
