package ir.asparsa.hobbytaste.ui.list.holder

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import butterknife.ButterKnife
import com.github.kittinunf.reactiveandroid.view.rx_click
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import ir.asparsa.android.core.logger.L
import ir.asparsa.android.ui.list.holder.BaseViewHolder
import ir.asparsa.hobbytaste.ApplicationLauncher
import ir.asparsa.hobbytaste.R
import ir.asparsa.hobbytaste.database.model.BannerModel
import ir.asparsa.hobbytaste.ui.list.HorizontalSpaceItemDecoration
import ir.asparsa.hobbytaste.ui.list.data.GalleryData
import kotterknife.bindView
import rx.Observable
import rx.subjects.PublishSubject
import rx.subjects.Subject
import javax.inject.Inject

/**
 * @author hadi
 */
class GalleryViewHolder(
        itemView: View
) : BaseViewHolder<GalleryData>(itemView) {

    @Inject
    lateinit var mPicasso: Picasso

    private val mAdapter: Adapter

    val mRecyclerView: RecyclerView by bindView(R.id.gallery_recycler)

    init {
        ApplicationLauncher.mainComponent().inject(this)
        ButterKnife.bind(this, itemView)

        val layoutManager = LinearLayoutManager(
                itemView.context, LinearLayoutManager.HORIZONTAL, false)

        mAdapter = Adapter()
        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.adapter = mAdapter

        mRecyclerView.addItemDecoration(HorizontalSpaceItemDecoration(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f,
                        itemView.context.resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f,
                        itemView.context.resources.displayMetrics).toInt()))
    }

    override fun onBindView(data: GalleryData) {
        super.onBindView(data)
        mAdapter.banners = data.banners
        mAdapter.notifyDataSetChanged()
    }

    private inner class Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var subject: Subject<BannerModel, BannerModel> = PublishSubject.create<BannerModel>()
        var banners: List<BannerModel> = ArrayList()

        override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
        ): RecyclerView.ViewHolder {
            val holder = ViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
            holder.clickStream().subscribe(subject)
            return holder
        }

        override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int
        ) {
            (holder as? ViewHolder)?.onBindView(banners[position])
        }

        override fun getItemCount(): Int {
            return banners.size
        }

        override fun getItemViewType(position: Int): Int {
            return R.layout.shot
        }
    }

    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), Target {

        val mShotLayout: FrameLayout by bindView(R.id.shot_layout)
        val mScreenshot: ImageView by bindView(R.id.shot)
        val mTryAgain: ImageView by bindView(R.id.try_again)
        val mProgressBar: ProgressBar by bindView(R.id.progress_bar)

        private var urlString: String? = null
        private var banner: BannerModel? = null

        init {
            ButterKnife.bind(this, itemView)

            mTryAgain.drawable.mutate()
                    .setColorFilter(itemView.resources.getColor(R.color.refresh), PorterDuff.Mode.SRC_IN)
        }

        fun onBindView(banner: BannerModel) {
            this.banner = banner
            L.i(javaClass, "mainUrl: " + banner.mainUrl + ", thumbnailUrl: " + banner.thumbnailUrl)
            if (TextUtils.isEmpty(banner.thumbnailUrl)) {
                if (TextUtils.isEmpty(banner.mainUrl)) {
                    itemView.visibility = View.GONE
                } else {
                    setupScreenshot(banner.mainUrl)
                }
            } else {
                setupScreenshot(banner.thumbnailUrl)
            }
        }

        private fun setupScreenshot(urlString: String) {
            this.urlString = urlString
            mPicasso.load(urlString)
                    .into(this)
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
            mProgressBar.visibility = View.GONE
            mTryAgain.visibility = View.VISIBLE
            mTryAgain.setOnClickListener {
                mProgressBar.visibility = View.VISIBLE
                mTryAgain.visibility = View.GONE
                setupScreenshot(urlString!!)
            }
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            mProgressBar.visibility = View.GONE
            mTryAgain.visibility = View.GONE
            if (bitmap == null) {
                return
            }
            val params = mScreenshot.layoutParams
            params.height = mScreenshot.resources.getDimensionPixelSize(R.dimen.shot_height)
            params.width = (params.height.toFloat() * bitmap.width / bitmap.height).toInt()
            mScreenshot.layoutParams = params
            mScreenshot.setImageBitmap(bitmap)
        }

        fun clickStream(): Observable<BannerModel> {
            return mShotLayout.rx_click().map {
                banner
            }
        }
    }

    fun clickStream(): Observable<BannerModel> {
        return mAdapter.subject
    }
}
