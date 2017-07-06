package ir.asparsa.hobbytaste.ui.list.provider;

import android.content.Context;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.data.DataObserver;
import ir.asparsa.android.ui.list.provider.AbsListProvider;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.AuthorizationManager;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;
import ir.asparsa.hobbytaste.ui.list.data.AboutUsData;
import ir.asparsa.hobbytaste.ui.list.data.LanguageData;
import ir.asparsa.hobbytaste.ui.list.data.UsernameData;

import javax.inject.Inject;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Locale;

/**
 * @author hadi
 */
public class SettingsProvider extends AbsListProvider {

    @Inject
    AuthorizationManager mAuthorizationManager;
    @Inject
    Context mContext;
    @Inject
    LanguageUtil mLanguageUtil;

    public SettingsProvider(
            RecyclerListAdapter adapter,
            BaseRecyclerFragment.OnInsertData onInsertData
    ) {

        super(adapter, onInsertData);
        ApplicationLauncher.mainComponent().inject(this);
        final Collection<BaseRecyclerData> collection = new ArrayDeque<>();
        collection.add(new UsernameData(mAuthorizationManager.getUsername()));
        collection.add(new LanguageData(
                mLanguageUtil.getLanguageTitle(mContext.getResources()),
                Locale.getDefault().getLanguage()));
        collection.add(new AboutUsData());

        mOnInsertData.onDataInserted(true, new DataObserver(true) {
            @Override public void onCompleted() {
                for (BaseRecyclerData data : collection) {
                    deque.add(data);
                }
            }

            @Override public void onNext(BaseRecyclerData baseRecyclerData) {
            }
        });
    }

    @Override public void provideData(
            long offset,
            int limit
    ) {
    }
}
