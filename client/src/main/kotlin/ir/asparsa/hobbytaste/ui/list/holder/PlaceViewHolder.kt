package ir.asparsa.hobbytaste.ui.list.holder

import android.os.Bundle
import android.view.View
import android.widget.TextView
import butterknife.ButterKnife
import com.github.kittinunf.reactiveandroid.view.rx_click
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ir.asparsa.android.core.logger.L
import ir.asparsa.android.core.util.UiUtil
import ir.asparsa.android.ui.list.holder.BaseViewHolder
import ir.asparsa.hobbytaste.ApplicationLauncher
import ir.asparsa.hobbytaste.R
import ir.asparsa.hobbytaste.core.util.MapUtil
import ir.asparsa.hobbytaste.database.model.StoreModel
import ir.asparsa.hobbytaste.ui.list.data.PlaceData
import kotterknife.bindView
import rx.Observable
import javax.inject.Inject

/**
 * @author hadi
 */
class PlaceViewHolder(
        itemView: View
) : BaseViewHolder<PlaceData>(itemView), OnMapReadyCallback {

    @Inject
    lateinit var mMapUtil: MapUtil
    @Inject
    lateinit var mUiUtil: UiUtil

    val mMapView: MapView by bindView(R.id.map)
    val mTitleView: TextView by bindView(R.id.title)
    val mDescriptionView: TextView by bindView(R.id.description)

    private var mMap: GoogleMap? = null
    private var mStore: StoreModel? = null
    private var mMarker: Marker? = null
    private var mIsCameraMovedBefore = false

    init {
        ApplicationLauncher.mainComponent().inject(this)
        ButterKnife.bind(this, itemView)
    }

    override fun onBindView(data: PlaceData) {
        super.onBindView(data)
        L.d(javaClass, "On bind gets called")
        mStore = data.storeModel

        mTitleView.text = mStore!!.title
        mDescriptionView.text = mStore!!.description
        publish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO why savedInstanceState is not working here?
        mMapView.onCreate(null)
        mMapView.onResume()
        mMapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        L.i(this.javaClass, "On map ready called")
        mMap = googleMap
        mMap!!.uiSettings.setAllGesturesEnabled(false)
        publish()
    }

    private fun publish() {
        if (mMap != null && mStore != null) {
            val latLng = LatLng(mStore!!.lat, mStore!!.lon)
            val icon = BitmapDescriptorFactory
                    .fromBitmap(mUiUtil.getBitmapFromVectorDrawable(R.drawable.ic_placeholder))

            mMarker = mMap!!.addMarker(
                    MarkerOptions()
                            .position(latLng)
                            .title(mStore!!.title)
                            .icon(icon)
            )

            if (!mIsCameraMovedBefore) {
                mIsCameraMovedBefore = true
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMapUtil.zoom(mMap, latLng)
            }
        }
    }

    fun clickStream(): Observable<PlaceData> {
        return itemView!!.rx_click().map { data }
    }

    override fun onResume() {
        mMapView.onResume()
    }

    override fun onStart() {
        // TODO uncomment it after updating google services
        //        mMapView.onStart();
    }

    override fun onPause() {
        mMapView.onPause()
    }

    override fun onStop() {
        // TODO uncomment it after updating google services
        //        mMapView.onStop();
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mMapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        mMapView.onDestroy()
    }
}
