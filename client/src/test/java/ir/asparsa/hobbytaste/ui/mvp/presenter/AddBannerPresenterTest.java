package ir.asparsa.hobbytaste.ui.mvp.presenter;

import android.os.Bundle;
import ir.asparsa.android.ui.view.DialogControlLayout;
import ir.asparsa.hobbytaste.database.model.BannerModel;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.fragment.content.AddBannerContentFragment;
import ir.asparsa.hobbytaste.ui.fragment.content.FragmentDelegate;
import ir.asparsa.hobbytaste.ui.mvp.holder.AddBannerViewHolder;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author hadi
 * @since 4/24/2017 AD.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddBannerPresenterTest extends BasePresenterTest {

    @Mock
    AddBannerViewHolder addBannerViewHolder;
    @Mock
    FragmentDelegate fragmentDelegate;
    @Mock
    Bundle bundle;

    @Test
    public void bindViewWithoutBitmapAndBannerTest() throws Exception {
        AddBannerPresenter presenter = new AddBannerPresenter(fragmentDelegate);

        when(fragmentDelegate.getArguments())
                .thenReturn(bundle);
        StoreModel store = Mockito.mock(StoreModel.class);
        ArrayList<BannerModel> banners = new ArrayList<>();
        when(store.getBanners())
                .thenReturn(banners);
        when(bundle.getParcelable(eq(AddBannerContentFragment.BUNDLE_KEY_STORE)))
                .thenReturn(store);

        presenter.bindView(addBannerViewHolder);

        verify(bundle, times(1)).getParcelable(eq(AddBannerContentFragment.BUNDLE_KEY_STORE));
        verify(bundle, times(1)).getString(eq(AddBannerPresenter.BUNDLE_KEY_BITMAP_FILE_PATH));
        verify(bundle, times(1)).getParcelable(eq(AddBannerPresenter.BUNDLE_KEY_BANNER));
        verify(addBannerViewHolder, times(1))
                .setupControllerButtons(anyBoolean(), anyBoolean(), any(DialogControlLayout.OnControlListener.class));

    }

    @Test
    public void bindViewWithBitmapAndWithoutBannerTest() throws Exception {
        AddBannerPresenter presenter = new AddBannerPresenter(fragmentDelegate);

        when(fragmentDelegate.getArguments())
                .thenReturn(bundle);
        String string = "any string";
        when(fragmentDelegate.getString(anyInt()))
                .thenReturn(string);

        StoreModel store = Mockito.mock(StoreModel.class);
        ArrayList<BannerModel> banners = new ArrayList<>();
        when(store.getBanners())
                .thenReturn(banners);
        when(bundle.getParcelable(eq(AddBannerContentFragment.BUNDLE_KEY_STORE)))
                .thenReturn(store);
        String filePath = "Some path";
        when(bundle.getString(eq(AddBannerPresenter.BUNDLE_KEY_BITMAP_FILE_PATH)))
                .thenReturn(filePath);


        presenter.bindView(addBannerViewHolder);

        verify(bundle, times(1)).getParcelable(eq(AddBannerContentFragment.BUNDLE_KEY_STORE));
        verify(bundle, times(1)).getString(eq(AddBannerPresenter.BUNDLE_KEY_BITMAP_FILE_PATH));
        verify(bundle, times(1)).getParcelable(eq(AddBannerPresenter.BUNDLE_KEY_BANNER));
        verify(addBannerViewHolder, times(1))
                .setupControllerButtons(anyBoolean(), anyBoolean(), any(DialogControlLayout.OnControlListener.class));
        verify(addBannerViewHolder, times(1)).setImageBitmap(eq(filePath));
        verify(addBannerViewHolder, times(1)).setHintText(eq(string));
        verify(addBannerViewHolder, times(1)).dismissProgressDialog();

    }

    @Test
    public void bindViewWithBitmapAndWithBannerTest() throws Exception {
        AddBannerPresenter presenter = new AddBannerPresenter(fragmentDelegate);

        when(fragmentDelegate.getArguments())
                .thenReturn(bundle);
        String string = "any string";
        when(fragmentDelegate.getString(anyInt()))
                .thenReturn(string);

        StoreModel store = Mockito.mock(StoreModel.class);
        ArrayList<BannerModel> banners = new ArrayList<>();
        BannerModel banner = Mockito.mock(BannerModel.class);
        banners.add(banner);
        when(store.getBanners())
                .thenReturn(banners);
        when(bundle.getParcelable(eq(AddBannerContentFragment.BUNDLE_KEY_STORE)))
                .thenReturn(store);
        String filePath = "Some path";
        when(bundle.getString(eq(AddBannerPresenter.BUNDLE_KEY_BITMAP_FILE_PATH)))
                .thenReturn(filePath);
        when(bundle.getParcelable(eq(AddBannerPresenter.BUNDLE_KEY_BANNER)))
                .thenReturn(banner);

        presenter.bindView(addBannerViewHolder);

        verify(bundle, times(1)).getParcelable(eq(AddBannerContentFragment.BUNDLE_KEY_STORE));
        verify(bundle, times(1)).getString(eq(AddBannerPresenter.BUNDLE_KEY_BITMAP_FILE_PATH));
        verify(bundle, times(1)).getParcelable(eq(AddBannerPresenter.BUNDLE_KEY_BANNER));
        verify(addBannerViewHolder, times(1))
                .setupControllerButtons(anyBoolean(), anyBoolean(), any(DialogControlLayout.OnControlListener.class));
        verify(addBannerViewHolder, times(1)).setImageBitmap(eq(filePath));
        verify(addBannerViewHolder, times(2)).setHintText(eq(string));
        verify(addBannerViewHolder, times(1)).dismissProgressDialog();
        verify(addBannerViewHolder, times(1)).dismissLoadingProgressDialog();

        Assert.assertFalse(banners.contains(banner));

    }

    @Test
    public void publishTest() throws Exception {
        AddBannerPresenter presenter = new AddBannerPresenter(fragmentDelegate);

        presenter.publish();

        verify(bundle, times(0)).getParcelable(eq(AddBannerContentFragment.BUNDLE_KEY_STORE));
        verify(bundle, times(0)).getString(eq(AddBannerPresenter.BUNDLE_KEY_BITMAP_FILE_PATH));
        verify(bundle, times(0)).getParcelable(eq(AddBannerPresenter.BUNDLE_KEY_BANNER));
        verify(addBannerViewHolder, times(0))
                .setupControllerButtons(anyBoolean(), anyBoolean(), any(DialogControlLayout.OnControlListener.class));
        verify(addBannerViewHolder, times(0)).dismissProgressDialog();
        verify(addBannerViewHolder, times(0)).dismissLoadingProgressDialog();

    }
}