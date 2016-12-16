package ir.asparsa.hobbytaste.ui.fragment.recycler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.dialog.BaseDialogFragment;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.android.ui.list.provider.AbsListProvider;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.AuthorizationManager;
import ir.asparsa.hobbytaste.ui.fragment.dialog.SetUsernameDialogFragment;
import ir.asparsa.hobbytaste.ui.list.data.UsernameData;
import ir.asparsa.hobbytaste.ui.list.holder.UserNameViewHolder;
import ir.asparsa.hobbytaste.ui.list.provider.SettingsProvider;

import javax.inject.Inject;

/**
 * Created by hadi on 12/14/2016 AD.
 */
public class SettingsRecyclerFragment extends BaseRecyclerFragment {

    @Inject
    AuthorizationManager mAuthorizationManager;

    public static SettingsRecyclerFragment instantiate() {
        Bundle bundle = new Bundle();
        SettingsRecyclerFragment fragment = new SettingsRecyclerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);
    }

    @Nullable @Override protected View getEmptyView() {
        return null;
    }

    @Override protected AbsListProvider provideDataList(
            RecyclerListAdapter adapter,
            OnInsertData insertData
    ) {
        return new SettingsProvider(adapter, insertData);
    }

    @Override protected OnEventListener getOnEventListener() {
        return new OnEventListener() {
            @Override public void onEvent(
                    int subscriber,
                    @Nullable Bundle bundle
            ) {
                switch (subscriber) {
                    case BaseViewHolder.EVENT_CLICK_USERNAME:
                        onUsernameClick();
                        break;
                }
            }
        };
    }

    @Override protected SparseArrayCompat<Class<? extends BaseViewHolder>> getViewHoldersList() {
        SparseArrayCompat<Class<? extends BaseViewHolder>> array = super.getViewHoldersList();
        array.put(UsernameData.VIEW_TYPE, UserNameViewHolder.class.asSubclass(BaseViewHolder.class));
        return array;
    }

    private void onUsernameClick() {
        SetUsernameDialogFragment.instantiate(
                mAuthorizationManager.getUsername(),
                new SetUsernameDialogFragment.OnSetUsernameDialogResultEvent(getTagName())
        ).show(getFragmentManager());
    }

    @Override public void onEvent(BaseEvent event) {
        L.i(this.getClass(), "event received: ");
        if (event instanceof BaseDialogFragment.BaseOnDialogResultEvent &&
            ((BaseDialogFragment.BaseOnDialogResultEvent) event).getDialogResult() !=
            BaseDialogFragment.DialogResult.COMMIT) {
            return;
        }

        if (event instanceof SetUsernameDialogFragment.OnSetUsernameDialogResultEvent) {
            SetUsernameDialogFragment.OnSetUsernameDialogResultEvent usernameEvent
                    = (SetUsernameDialogFragment.OnSetUsernameDialogResultEvent) event;
            L.i(this.getClass(), "username event received: " + usernameEvent.getUsername());
        }
    }
}
