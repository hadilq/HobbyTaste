package ir.asparsa.hobbytaste.ui.list.provider;

import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.data.DataObserver;
import ir.asparsa.android.ui.list.provider.AbsListProvider;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.AuthorizationManager;
import ir.asparsa.hobbytaste.ui.list.data.UsernameData;

import javax.inject.Inject;

/**
 * Created by hadi on 12/14/2016 AD.
 */
public class SettingsProvider extends AbsListProvider {

    @Inject
    AuthorizationManager mAuthorizationManager;

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
        mOnInsertData.OnDataInserted(new DataObserver() {
            @Override public void onCompleted() {
                deque.add(new UsernameData(mAuthorizationManager.getUsername()));
            }

            @Override public void onNext(BaseRecyclerData baseRecyclerData) {

            }
        });
    }
}
