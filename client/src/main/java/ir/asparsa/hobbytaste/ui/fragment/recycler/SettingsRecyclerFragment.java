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
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;
import ir.asparsa.hobbytaste.core.util.LaunchUtil;
import ir.asparsa.hobbytaste.ui.activity.LaunchActivity;
import ir.asparsa.hobbytaste.ui.fragment.dialog.LanguageDialogFragment;
import ir.asparsa.hobbytaste.ui.fragment.dialog.SetUsernameDialogFragment;
import ir.asparsa.hobbytaste.ui.list.data.LanguageData;
import ir.asparsa.hobbytaste.ui.list.data.UsernameData;
import ir.asparsa.hobbytaste.ui.list.holder.LanguageViewHolder;
import ir.asparsa.hobbytaste.ui.list.holder.UserNameViewHolder;
import ir.asparsa.hobbytaste.ui.list.provider.SettingsProvider;
import rx.Observer;
import rx.subscriptions.CompositeSubscription;

import javax.inject.Inject;

/**
 * Created by hadi on 12/14/2016 AD.
 */
public class SettingsRecyclerFragment extends BaseRecyclerFragment<SettingsProvider> {

    @Inject
    AuthorizationManager mAuthorizationManager;
    @Inject
    PreferencesManager mPreferencesManager;

    private CompositeSubscription mSubscription = new CompositeSubscription();

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

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSubscription.add(mAuthorizationManager.register(new AuthorizationManager.OnUsernameChangeObserver() {
            @Override public void onNext(String s) {
                changeUsername(s);
            }
        }));
    }

    @Override public void onDestroyView() {
        mSubscription.clear();
        super.onDestroyView();
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
                } else if (t instanceof LanguageViewHolder.LanguageClick) {
                    onLanguageClick(((LanguageViewHolder.LanguageClick) t).getLangAbbreviation());
                }
            }
        };
    }

    @Override protected SparseArrayCompat<Class<? extends BaseViewHolder>> getViewHoldersList() {
        SparseArrayCompat<Class<? extends BaseViewHolder>> array = super.getViewHoldersList();
        array.put(UsernameData.VIEW_TYPE, UserNameViewHolder.class.asSubclass(BaseViewHolder.class));
        array.put(LanguageData.VIEW_TYPE, LanguageViewHolder.class.asSubclass(BaseViewHolder.class));
        return array;
    }

    private void onUsernameClick(String username) {
        SetUsernameDialogFragment.instantiate(
                username,
                new SetUsernameDialogFragment.OnSetUsernameDialogResultEvent(getTagName())
        ).show(getFragmentManager());
    }

    private void onLanguageClick(String language) {
        LanguageDialogFragment.instantiate(
                language,
                new LanguageDialogFragment.OnChangeLanguageDialogResultEvent(getTagName())
        ).show(getFragmentManager());
    }

    @Override public void onEvent(BaseEvent event) {
        L.i(this.getClass(), "event received ");
        if (event instanceof BaseDialogFragment.BaseOnDialogResultEvent &&
            ((BaseDialogFragment.BaseOnDialogResultEvent) event).getDialogResult() !=
            BaseDialogFragment.DialogResult.COMMIT) {
            return;
        }

        if (event instanceof SetUsernameDialogFragment.OnSetUsernameDialogResultEvent) {
            SetUsernameDialogFragment.OnSetUsernameDialogResultEvent usernameEvent
                    = (SetUsernameDialogFragment.OnSetUsernameDialogResultEvent) event;
            L.i(getClass(), "username event received: " + usernameEvent.getUsername());
            L.i(getClass(), "token changed: " + usernameEvent.getToken());
            mAuthorizationManager.setToken(usernameEvent.getToken());
            mAuthorizationManager.setUsername(usernameEvent.getUsername());
        } else if (event instanceof LanguageDialogFragment.OnChangeLanguageDialogResultEvent) {
            if (LanguageUtil.setDefaultLanguage(
                    mPreferencesManager,
                    ((LanguageDialogFragment.OnChangeLanguageDialogResultEvent) event).getLanguage())) {

                L.i(getClass(), "Language about to change");
                getActivity().finish();
                LaunchUtil.launch(getContext(), LaunchActivity.class);
            }
        }
    }

    private void changeUsername(String username) {
        for (BaseRecyclerData data : mAdapter.findData(UsernameData.class)) {
            ((UsernameData) data).setUsername(username);
            int index = mAdapter.getList().indexOf(data);
            if (index != -1) {
                mAdapter.notifyItemChanged(index);
            }
        }
    }
}
