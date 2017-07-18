package ir.asparsa.hobbytaste.ui.list.holder

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.ButterKnife
import com.github.kittinunf.reactiveandroid.view.rx_click
import ir.asparsa.android.ui.list.holder.BaseViewHolder
import ir.asparsa.hobbytaste.R
import ir.asparsa.hobbytaste.ui.list.data.LanguageData
import kotterknife.bindView
import rx.Observable

/**
 * @author hadi
 */
class LanguageViewHolder(
        itemView: View
) : BaseViewHolder<LanguageData>(itemView) {

    val mLayout: ViewGroup by bindView(R.id.language_layout)
    val mLanguageTextView: TextView by bindView(R.id.language)

    init {
        ButterKnife.bind(this, itemView)
    }

    override fun onBindView(data: LanguageData) {
        super.onBindView(data)
        mLanguageTextView.text = data.language
    }


    fun clickStream(): Observable<LanguageData> {
        return mLayout.rx_click().map { data }
    }
}
