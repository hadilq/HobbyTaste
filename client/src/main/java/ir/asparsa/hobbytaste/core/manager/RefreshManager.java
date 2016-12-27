package ir.asparsa.hobbytaste.core.manager;

import ir.asparsa.android.core.logger.L;
import ir.asparsa.hobbytaste.database.model.CommentModel;
import rx.Observer;
import rx.Subscription;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
@Singleton
public class RefreshManager {

    @Inject
    StoresManager mStoresManager;
    @Inject
    CommentManager mCommentManager;

    @Inject
    public RefreshManager() {
    }

    public void refreshStores() {
        mStoresManager.getRefreshable().refresh(null);
    }


    public void saveComment(
            CommentModel comment,
            final Observer<Void> observer
    ) {
        mCommentManager.saveComment(comment, observer);
    }

    public interface Refreshable {
        void refresh(Constraint constraint);
    }

    public interface Constraint {
    }


}
