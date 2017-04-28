package ir.asparsa.hobbytaste.ui.fragment.content;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import ir.asparsa.android.core.logger.L;
import ir.asparsa.android.ui.fragment.dialog.BaseDialogFragment;
import ir.asparsa.hobbytaste.ApplicationLauncher;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.route.PlaceRoute;
import ir.asparsa.hobbytaste.core.route.RouteFactory;
import ir.asparsa.hobbytaste.core.util.NavigationUtil;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.fragment.dialog.CommentDialogFragment;
import ir.asparsa.hobbytaste.ui.fragment.recycler.StoreDetailsRecyclerFragment;

import javax.inject.Inject;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
public class StoreDetailsContentFragment extends BaseContentFragment {

    @Inject
    RouteFactory mRouteFactory;

    public static StoreDetailsContentFragment instantiate(StoreModel store) {

        Bundle bundle = new Bundle();
        bundle.putParcelable(StoreDetailsRecyclerFragment.BUNDLE_KEY_STORE, store);
        StoreDetailsContentFragment fragment = new StoreDetailsContentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLauncher.mainComponent().inject(this);
        setHasOptionsMenu(true);
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Fragment fragment = NavigationUtil.getActiveFragment(getChildFragmentManager());
        if (!(fragment instanceof StoreDetailsRecyclerFragment)) {

            NavigationUtil.startNestedFragment(
                    getChildFragmentManager(),
                    StoreDetailsRecyclerFragment.instantiate(new Bundle(getArguments()))
            );
        }
    }

    @Override public void onCreateOptionsMenu(
            Menu menu,
            MenuInflater inflater
    ) {
        inflater.inflate(R.menu.menu_share, menu);
        MenuItem share = menu.findItem(R.id.share);
        share.getIcon().mutate()
             .setColorFilter(getResources().getColor(R.color.background), PorterDuff.Mode.SRC_ATOP);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Uri.Builder uriBuilder = mRouteFactory.getShareUriBuilder(getResources(), PlaceRoute.class);
                StoreModel store = getArguments().getParcelable(StoreDetailsRecyclerFragment.BUNDLE_KEY_STORE);
                if (uriBuilder == null || store == null) {
                    return true;
                }
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        uriBuilder.appendPath(Long.toString(store.getHashCode())).build().toString()
                );
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected String setHeaderTitle() {
        StoreModel store = getArguments().getParcelable(StoreDetailsRecyclerFragment.BUNDLE_KEY_STORE);
        if (store != null) {
            return store.getTitle();
        }
        return getString(R.string.title_store_details);
    }

    @Override public void onEvent(BaseEvent event) {
        L.i(getClass(), "event received: ");
        if (event instanceof BaseDialogFragment.BaseOnDialogResultEvent &&
            ((BaseDialogFragment.BaseOnDialogResultEvent) event).getDialogResult() !=
            BaseDialogFragment.DialogResult.COMMIT) {
            return;
        }

        if (event instanceof CommentDialogFragment.CommentDialogResultEvent) {
            CommentDialogFragment.CommentDialogResultEvent commentEvent
                    = (CommentDialogFragment.CommentDialogResultEvent) event;

            Fragment fragment = NavigationUtil.getActiveFragment(getChildFragmentManager());
            if (fragment instanceof StoreDetailsRecyclerFragment) {
                ((StoreDetailsRecyclerFragment) fragment).addComment(commentEvent.getComment());
            }
        }
    }

    @Override public FloatingActionButtonObserver getFloatingActionButtonObserver() {
        return new FloatingActionButtonObserver() {
            @Override public void onNext(View view) {
                StoreModel store = getArguments().getParcelable(StoreDetailsRecyclerFragment.BUNDLE_KEY_STORE);
                if (store == null) {
                    return;
                }
                CommentDialogFragment
                        .instantiate(store, new CommentDialogFragment.CommentDialogResultEvent(getTagName()))
                        .show(getFragmentManager());
            }
        };
    }

    @Override public boolean hasHomeAsUp() {
        return true;
    }

    @Override public boolean scrollToolbar() {
        return true;
    }
}
