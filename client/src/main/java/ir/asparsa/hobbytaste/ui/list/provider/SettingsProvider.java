package ir.asparsa.hobbytaste.ui.list.provider;

import android.content.Context;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.data.DataObserver;
import ir.asparsa.android.ui.list.provider.AbsListProvider;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.AuthorizationManager;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;
import ir.asparsa.hobbytaste.ui.list.data.LanguageData;
import ir.asparsa.hobbytaste.ui.list.data.UsernameData;

import javax.inject.Inject;
import java.util.Locale;

/**
 * @author hadi
 * @since 12/14/2016 AD.
 */
public class SettingsProvider extends AbsListProvider {

    @Inject
    AuthorizationManager mAuthorizationManager;
    @Inject
    Context mContext;

    public SettingsProvider(
            RecyclerListAdapter adapter,
            BaseRecyclerFragment.OnInsertData onInsertData
    ) {

        super(adapter, onInsertData);
        ApplicationLauncher.mainComponent().inject(this);
    }

    @Override public void provideData(
            long offset,
            int limit
    ) {
        L.i(getClass(), "New data needed: " + offset + " " + limit);
        mOnInsertData.OnDataInserted(new DataObserver() {
            @Override public void onCompleted() {
                deque.add(new UsernameData(mAuthorizationManager.getUsername()));
                deque.add(new LanguageData(
                        LanguageUtil.getLanguage(mContext.getResources()),
                        Locale.getDefault().getLanguage()));
            }

            @Override public void onNext(BaseRecyclerData baseRecyclerData) {

            }
        });
    }
}
