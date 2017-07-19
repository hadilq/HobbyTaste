package ir.asparsa.hobbytaste.ui.fragment.recycler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.dialog.BaseDialogFragment;
import ir.asparsa.android.ui.list.adapter.RecyclerListAdapter;
import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.core.manager.AuthorizationManager;
import ir.asparsa.hobbytaste.core.manager.PreferencesManager;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;
import ir.asparsa.hobbytaste.ui.activity.LaunchActivity;
import ir.asparsa.hobbytaste.ui.fragment.dialog.LanguageDialogFragment;
import ir.asparsa.hobbytaste.ui.fragment.dialog.SetUsernameDialogFragment;
import ir.asparsa.hobbytaste.ui.list.data.AboutUsData;
import ir.asparsa.hobbytaste.ui.list.data.LanguageData;
import ir.asparsa.hobbytaste.ui.list.data.UsernameData;
import ir.asparsa.hobbytaste.ui.list.holder.AboutUsViewHolder;
import ir.asparsa.hobbytaste.ui.list.holder.LanguageViewHolder;
import ir.asparsa.hobbytaste.ui.list.holder.UserNameViewHolder;
import ir.asparsa.hobbytaste.ui.list.provider.SettingsProvider;
import rx.functions.Action1;

import javax.inject.Inject;

/**
 * @author hadi
 */
public class SettingsRecyclerFragment extends AbsRecyclerFragment<SettingsProvider> {

    @Inject
    AuthorizationManager mAuthorizationManager;
    @Inject
    PreferencesManager mPreferencesManager;
    @Inject
    LanguageUtil mLanguageUtil;

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
        L.d(getClass(), "On activity created");
        super.onActivityCreated(savedInstanceState);
        mSubscription.add(mAuthorizationManager.register(new AuthorizationManager.OnUsernameChangeObserver() {
            @Override public void onNext(String s) {
                changeUsername(s);
            }
        }));
    }

    @Override protected SettingsProvider provideDataList(
            RecyclerListAdapter adapter,
            OnInsertData insertData
    ) {
        return new SettingsProvider(adapter, insertData);
    }

    @Override protected <T extends BaseViewHolder> Action1<T> getObserver() {
        return new Action1<T>() {
            @Override public void call(T t) {
                if (t instanceof UserNameViewHolder) {
                    ((UserNameViewHolder) t).clickStream().subscribe(getUsernameClickObserver());
                } else if (t instanceof LanguageViewHolder) {
                    ((LanguageViewHolder) t).clickStream().subscribe(getLanguageClickObserver());
                } else if (t instanceof AboutUsViewHolder) {
                    if (mContentObserver != null) {
                        mContentObserver.call(t);
                    }
                }
            }
        };
    }

    @Override protected SparseArrayCompat<Class<? extends BaseViewHolder>> getViewHoldersList() {
        SparseArrayCompat<Class<? extends BaseViewHolder>> array = super.getViewHoldersList();
        array.put(UsernameData.VIEW_TYPE, UserNameViewHolder.class.asSubclass(BaseViewHolder.class));
        array.put(LanguageData.VIEW_TYPE, LanguageViewHolder.class.asSubclass(BaseViewHolder.class));
        array.put(AboutUsData.VIEW_TYPE, AboutUsViewHolder.class.asSubclass(BaseViewHolder.class));
        return array;
    }

    private Action1<UsernameData> getUsernameClickObserver() {
        return new Action1<UsernameData>() {
            @Override public void call(UsernameData usernameData) {
                SetUsernameDialogFragment.instantiate(
                        usernameData.getUsername(),
                        new SetUsernameDialogFragment.OnSetUsernameDialogResultEvent(getTagName())
                ).show(getFragmentManager());
            }
        };
    }

    private Action1<LanguageData> getLanguageClickObserver() {
        return new Action1<LanguageData>() {
            @Override public void call(LanguageData languageData) {
                LanguageDialogFragment.instantiate(
                        languageData.getLanguage(),
                        new LanguageDialogFragment.OnChangeLanguageDialogResultEvent(getTagName())
                ).show(getFragmentManager());
            }
        };
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
            if (mLanguageUtil.setDefaultLanguage(
                    mPreferencesManager,
                    ((LanguageDialogFragment.OnChangeLanguageDialogResultEvent) event).getLanguage())) {

                L.i(getClass(), "Language about to change");
                getActivity().getIntent().putExtra(LaunchActivity.BUNDLE_KEY_CONFIGURATION_CHANGED, 100);
                getActivity().recreate();
            }
        }
    }

    private void changeUsername(String username) {
        L.i(getClass(), "On change username");
        for (BaseRecyclerData data : mAdapter.findData(UsernameData.class)) {
            L.d(getClass(), "Username data is found");
            ((UsernameData) data).setUsername(username);
            int index = mAdapter.getList().indexOf(data);
            if (index != -1) {
                mAdapter.notifyItemChanged(index);
            }
        }
    }
}
