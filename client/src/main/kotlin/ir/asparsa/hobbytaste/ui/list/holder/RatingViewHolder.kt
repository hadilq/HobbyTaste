package ir.asparsa.hobbytaste.ui.list.holder

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.ButterKnife
import com.github.kittinunf.reactiveandroid.view.rx_click
import ir.asparsa.android.ui.list.holder.BaseViewHolder
import ir.asparsa.hobbytaste.R
import ir.asparsa.hobbytaste.ui.list.data.RatingData
import kotterknife.bindView
import rx.Observable
import java.util.*

/**
 * @author hadi
 */
class RatingViewHolder(
        itemView: View
) : BaseViewHolder<RatingData>(itemView) {

    val mHeartImageView: ImageView by bindView(R.id.heart)
    val mArrowImageView: ImageView by bindView(R.id.arrow)
    val mRateTextView: TextView by bindView(R.id.rate)
    val mViewedTextView: TextView by bindView(R.id.viewed)
    val mDescriptionTextView: TextView by bindView(R.id.store_description)
    val mDashboardView: ViewGroup by bindView(R.id.dashboard)
    val mRateTailImageView: ImageView by bindView(R.id.rate_tail)
    val mViewedTailImageView: ImageView by bindView(R.id.viewed_tail)
    val mToggle: ViewGroup by bindView(R.id.toggle)
    val mCreatorTextView: TextView by bindView(R.id.store_creator)

    private val mUpArrow: Drawable
    private val mDownArrow: Drawable

    init {
        ButterKnife.bind(this, itemView)

        mUpArrow = itemView.context.resources.getDrawable(R.drawable.ic_arrow_drop_up)
        mDownArrow = itemView.context.resources.getDrawable(R.drawable.ic_arrow_drop_down)
        mUpArrow.mutate().setColorFilter(itemView.resources.getColor(R.color.refresh), PorterDuff.Mode.SRC_IN)
        mDownArrow.mutate().setColorFilter(itemView.resources.getColor(R.color.refresh), PorterDuff.Mode.SRC_IN)
        mUpArrow.setColorFilter(itemView.resources.getColor(R.color.dark_background), PorterDuff.Mode.SRC_ATOP)
        mDownArrow.setColorFilter(itemView.resources.getColor(R.color.dark_background), PorterDuff.Mode.SRC_ATOP)

        mRateTailImageView.drawable.mutate()
                .setColorFilter(itemView.resources.getColor(R.color.heart_on), PorterDuff.Mode.SRC_ATOP)
        mViewedTailImageView.drawable.mutate()
                .setColorFilter(
                        itemView.resources.getColor(R.color.dark_background),
                        PorterDuff.Mode.SRC_ATOP)
    }

    override fun onBindView(data: RatingData) {
        super.onBindView(data)
        mRateTextView.text = String.format(Locale.getDefault(), "%d", data.rate)

        mViewedTextView.text = String.format(Locale.getDefault(), "%d", data.viewed)

        mHeartImageView.drawable.mutate().setColorFilter(
                itemView.resources.getColor(if (data.isLike) R.color.heart_on else R.color.heart_off),
                PorterDuff.Mode.SRC_ATOP)

        mDescriptionTextView.text = data.description
        mCreatorTextView.text = data.creator
        if (data.isShowDescription) {
            mArrowImageView.setImageDrawable(mUpArrow)
            mToggle.visibility = View.VISIBLE
        } else {
            mArrowImageView.setImageDrawable(mDownArrow)
            mToggle.visibility = View.GONE
        }
    }

    fun clickHeartStream(): Observable<RatingData> {
        return mHeartImageView.rx_click().map { data }
    }

    fun clickDashboardStream(): Observable<RatingData> {
        return mDashboardView.rx_click().map { data }
    }
}
