package ir.asparsa.hobbytaste.ui.list.holder;

import android.support.annotation.NonNull;
import android.view.View;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.android.ui.list.holder.BaseViewHolderFactory;
import ir.asparsa.android.ui.list.holder.CoreViewHolderFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author hadi
 */
@Singleton
public class ViewHolderFactory implements BaseViewHolderFactory {

    @Inject
    CoreViewHolderFactory mFactory;

    @Inject ViewHolderFactory() {
    }

    @NonNull
    public BaseViewHolder create(
            @NonNull Class<? extends BaseViewHolder> clazz,
            @NonNull View itemView
    ) {
        if (clazz.isAssignableFrom(AboutUsViewHolder.class)) {
            return new AboutUsViewHolder(itemView);
        } else if (clazz.isAssignableFrom(CommentViewHolder.class)) {
            return new CommentViewHolder(itemView);
        } else if (clazz.isAssignableFrom(GalleryViewHolder.class)) {
            return new GalleryViewHolder(itemView);
        } else if (clazz.isAssignableFrom(LanguageViewHolder.class)) {
            return new LanguageViewHolder(itemView);
        } else if (clazz.isAssignableFrom(PlaceViewHolder.class)) {
            return new PlaceViewHolder(itemView);
        } else if (clazz.isAssignableFrom(RatingViewHolder.class)) {
            return new RatingViewHolder(itemView);
        } else if (clazz.isAssignableFrom(UserNameViewHolder.class)) {
            return new UserNameViewHolder(itemView);
        } else if (clazz.isAssignableFrom(StoreMapViewHolder.class)) {
            return new StoreMapViewHolder(itemView);
        }
        return mFactory.create(clazz, itemView);
    }
}
