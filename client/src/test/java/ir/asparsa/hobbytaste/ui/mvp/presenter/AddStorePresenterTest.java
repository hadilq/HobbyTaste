package ir.asparsa.hobbytaste.ui.mvp.presenter;

import android.os.Bundle;
import android.os.Parcelable;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import ir.asparsa.hobbytaste.database.model.StoreModel;
import ir.asparsa.hobbytaste.ui.mvp.holder.AddStoreContentViewHolder;
import ir.asparsa.hobbytaste.ui.mvp.holder.FragmentHolder;
import ir.asparsa.hobbytaste.ui.wrappers.WCameraPosition;
import ir.asparsa.hobbytaste.ui.wrappers.WMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * @author hadi
 * @since 4/19/2017 AD.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddStorePresenterTest extends BasePresenterTest {

    @Mock
    AddStoreContentViewHolder addStoreContentViewHolder;
    @Mock
    FragmentHolder fragmentHolder;
    @Mock
    Bundle bundle;
    @Mock
    WMap map;
    @Mock
    WCameraPosition cameraPosition;

    @Test
    public void bindViewTest() throws Exception {

        AddStorePresenter presenter = new AddStorePresenter(fragmentHolder);

        presenter.bindView(addStoreContentViewHolder);

        verify(addStoreContentViewHolder, times(1)).getMap();

        when(addStoreContentViewHolder.getMap())
                .thenReturn(map);
        when(fragmentHolder.getArguments())
                .thenReturn(bundle);
        when(bundle.getParcelable(eq(AddStorePresenter.BUNDLE_KEY_CAMERA_POSITION)))
                .thenReturn(CameraPosition.fromLatLngZoom(new LatLng(20, 34), 5));
        assert fragmentHolder.getArguments().getParcelable(AddStorePresenter.BUNDLE_KEY_CAMERA_POSITION) != null;

        presenter.bindView(addStoreContentViewHolder);

        verify(map, times(1)).moveCamera(any(CameraPosition.class));
        verify(bundle, times(4)).getParcelable(anyString());

        when(map.getCameraPosition())
                .thenReturn(cameraPosition);

        presenter.unbindView();

        verify(bundle, times(1)).putParcelable(anyString(), nullable(Parcelable.class));
    }

    @Test
    public void publishTest() throws Exception {

        AddStorePresenter presenter = new AddStorePresenter(fragmentHolder);

        when(addStoreContentViewHolder.getMap())
                .thenReturn(map);
        when(fragmentHolder.getArguments())
                .thenReturn(bundle);
        when(bundle.getParcelable(eq(AddStorePresenter.BUNDLE_KEY_CAMERA_POSITION)))
                .thenReturn(CameraPosition.fromLatLngZoom(new LatLng(20, 34), 5));
        assert fragmentHolder.getArguments().getParcelable(AddStorePresenter.BUNDLE_KEY_CAMERA_POSITION) != null;

        StoreModel store = Mockito.mock(StoreModel.class);
        when(bundle.getParcelable(eq(AddStorePresenter.BUNDLE_KEY_STORE)))
                .thenReturn(store);

        presenter.bindView(addStoreContentViewHolder);

        verify(map, times(1)).moveCamera(any(CameraPosition.class));
        verify(bundle, times(4)).getParcelable(anyString());
        verify(addStoreContentViewHolder, times(1)).setMarker(any(LatLng.class));

        when(map.getCameraPosition())
                .thenReturn(cameraPosition);

        presenter.unbindView();

        verify(bundle, times(1)).putParcelable(anyString(), nullable(Parcelable.class));
    }
}