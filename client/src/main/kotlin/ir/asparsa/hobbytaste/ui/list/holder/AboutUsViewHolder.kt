package ir.asparsa.hobbytaste.ui.list.holder

import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.github.kittinunf.reactiveandroid.view.rx_click
import ir.asparsa.android.ui.list.holder.BaseViewHolder
import ir.asparsa.hobbytaste.R
import ir.asparsa.hobbytaste.ui.list.data.AboutUsData
import kotterknife.bindView
import rx.Observable

/**
 * @author hadi
 */
class AboutUsViewHolder(
        itemView: View
) : BaseViewHolder<AboutUsData>(itemView) {

    val mLayout: ViewGroup by bindView(R.id.about_us_layout)

    init {
        ButterKnife.bind(this, itemView)
    }

    fun clickStream(): Observable<AboutUsData> {
        return mLayout.rx_click().map { data }
    }
}
