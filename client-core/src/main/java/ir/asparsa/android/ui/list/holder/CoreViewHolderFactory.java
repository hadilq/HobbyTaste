package ir.asparsa.android.ui.list.holder;

import android.support.annotation.NonNull;
import android.view.View;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author hadi
 */
@Singleton
public class CoreViewHolderFactory implements BaseViewHolderFactory {

    @Inject CoreViewHolderFactory() {
    }

    @NonNull @Override public BaseViewHolder create(
            @NonNull Class<? extends BaseViewHolder> clazz,
            @NonNull View itemView
    ) {
        if (clazz.isAssignableFrom(TryAgainViewHolder.class)) {
            return new TryAgainViewHolder(itemView);
        }
        return new EmptyViewHolder(new View(itemView.getContext()));
    }
}
