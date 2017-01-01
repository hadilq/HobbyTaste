package ir.asparsa.hobbytaste.ui.fragment.recycler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.dialog.BaseDialogFragment;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.AuthorizationManager;
import ir.asparsa.hobbytaste.ui.fragment.dialog.SetUsernameDialogFragment;
import ir.asparsa.hobbytaste.ui.list.data.UsernameData;
import ir.asparsa.hobbytaste.ui.list.holder.UserNameViewHolder;
import ir.asparsa.hobbytaste.ui.list.provider.SettingsProvider;
import rx.Observer;

import javax.inject.Inject;

/**
 * Created by hadi on 12/14/2016 AD.
 */
public class SettingsRecyclerFragment extends BaseRecyclerFragment<SettingsProvider> {

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

    @Override protected SettingsProvider provideDataList(
            RecyclerListAdapter adapter,
            OnInsertData insertData
    ) {
        return new SettingsProvider(adapter, insertData);
    }

    @Override protected <T extends Event> Observer<T> getObserver() {
        return new Observer<T>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(T t) {
                if (t instanceof UserNameViewHolder.UsernameClick) {
                    onUsernameClick(((UserNameViewHolder.UsernameClick) t).getUsername());
                }
            }
        };
    }

    @Override protected SparseArrayCompat<Class<? extends BaseViewHolder>> getViewHoldersList() {
        SparseArrayCompat<Class<? extends BaseViewHolder>> array = super.getViewHoldersList();
        array.put(UsernameData.VIEW_TYPE, UserNameViewHolder.class.asSubclass(BaseViewHolder.class));
        return array;
    }

    private void onUsernameClick(String username) {
        SetUsernameDialogFragment.instantiate(
                username,
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
            L.i(this.getClass(), "token changed: " + usernameEvent.getToken());
            mAuthorizationManager.setUsername(usernameEvent.getUsername());
            mAuthorizationManager.setToken(usernameEvent.getToken());

            for (BaseRecyclerData data : mAdapter.findData(UsernameData.class)) {
                ((UsernameData) data).setUsername(usernameEvent.getUsername());
            }

            for (Integer index : mAdapter.findViewHolder(UserNameViewHolder.class)) {
                mAdapter.notifyItemChanged(index);
            }
        }
    }
}