package ir.asparsa.hobbytaste.ui.mvp.presenter;

import android.support.annotation.NonNull;
import ir.asparsa.hobbytaste.ui.mvp.holder.ViewHolder;

/**
 * @author hadi
 * @since 4/14/2017 AD.
 */
public interface Presenter<H extends ViewHolder> {

    void bindView(@NonNull H viewHolder);

    void unbindView();

    void publish();
}
