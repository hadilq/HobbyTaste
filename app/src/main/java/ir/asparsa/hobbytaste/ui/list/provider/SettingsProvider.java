package ir.asparsa.hobbytaste.ui.list.provider;

import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.provider.AbsListProvider;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.AccountManager;
import ir.asparsa.hobbytaste.ui.list.data.UsernameData;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hadi on 12/14/2016 AD.
 */
public class SettingsProvider extends AbsListProvider {

    @Inject
    AccountManager mAccountManager;

    public SettingsProvider(
            RecyclerListAdapter adapter,
            BaseRecyclerFragment.OnInsertData onInsertData
    ) {
        super(adapter, onInsertData);
        ApplicationLauncher.mainComponent().inject(this);
    }

    @Override public void provideData(
            long limit,
            long offset
    ) {
        List<BaseRecyclerData> list = new ArrayList<>();
        list.add(new UsernameData(mAccountManager.getUsername()));

        mOnInsertData.OnDataInserted(true, list);
    }
}
