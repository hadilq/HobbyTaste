package ir.asparsa.hobbytaste.ui.mvp.presenter;

import android.os.Bundle;
import ir.asparsa.hobbytaste.core.manager.StoresManager;
import ir.asparsa.hobbytaste.ui.mvp.holder.FragmentHolder;
import ir.asparsa.hobbytaste.ui.mvp.holder.MainContentViewHolder;
import ir.asparsa.hobbytaste.ui.wrappers.WCameraPosition;
import ir.asparsa.hobbytaste.ui.wrappers.WLatLng;
import ir.asparsa.hobbytaste.ui.wrappers.WMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import rx.Observer;
import rx.Subscription;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.*;

/**
 * @author hadi
 * @since 4/14/2017 AD.
 */
@RunWith(MockitoJUnitRunner.class)
public class StorePresenterTest extends BasePresenterTest {

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    MainContentViewHolder mainContentViewHolder;
    @Mock
    Subscription subscription;
    @Mock
    FragmentHolder fragmentHolder;
    @Mock
    Bundle bundle;

    @Test
    public void bindViewTest() {
        when(preferencesManager.getFloat(anyString(), anyFloat()))
                .thenReturn(0f);

        when(preferencesManager.getFloat(anyString(), anyFloat()))
                .thenReturn(0f);
        when(storesManager.loadStores(any(StoresManager.Constraint.class), any(Observer.class)))
                .thenReturn(subscription);
        when(fragmentHolder.getArguments())
                .thenReturn(bundle);

        StorePresenter presenter = new StorePresenter(fragmentHolder);
        presenter.bindView(mainContentViewHolder);

        verify(fragmentHolder, times(2)).getArguments();
        verify(bundle, times(1)).getParcelableArrayList(anyString());
        verify(bundle, times(1)).getInt(anyString(), anyInt());
        verify(storesManager, times(1)).loadStores(any(StoresManager.Constraint.class), any(Observer.class));

        WMap map = Mockito.mock(WMap.class);
        WCameraPosition position = Mockito.mock(WCameraPosition.class);
        WLatLng target = Mockito.mock(WLatLng.class);
        when(mainContentViewHolder.getMap())
                .thenReturn(map);
        when(map.getCameraPosition())
                .thenReturn(position);
        when(position.getTarget())
                .thenReturn(target);

        presenter.unbindView();

        verify(preferencesManager, times(3)).put(anyString(), anyFloat());
        verify(mainContentViewHolder, times(1)).removeMarkers(any(List.class));
        verify(bundle, times(1)).putInt(anyString(), anyInt());
        verify(bundle, times(1)).putParcelableArrayList(anyString(), any(ArrayList.class));
        verify(target, times(1)).getLatitude();
        verify(target, times(1)).getLongitude();
        verify(position, times(1)).getZoom();

        presenter.bindView(mainContentViewHolder);

        verify(mainContentViewHolder, times(2)).getMap();
    }
}