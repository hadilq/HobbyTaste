package ir.asparsa.hobbytaste.ui.list.holder

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.ButterKnife
import com.github.kittinunf.reactiveandroid.view.rx_click
import ir.asparsa.android.core.logger.L
import ir.asparsa.android.ui.list.holder.BaseViewHolder
import ir.asparsa.hobbytaste.R
import ir.asparsa.hobbytaste.ui.list.data.UsernameData
import kotterknife.bindView
import rx.Observable

/**
 * @author hadi
 */
class UserNameViewHolder(
        itemView: View
) : BaseViewHolder<UsernameData>(itemView) {

    val mLayout: ViewGroup  by bindView(R.id.username_layout)
    val mUsernameTextView: TextView by bindView(R.id.username)

    init {
        ButterKnife.bind(this, itemView)
        L.d(javaClass, "On bind gets called")
    }

    override fun onBindView(data: UsernameData) {
        super.onBindView(data)
        L.d(javaClass, "On bind gets called")
        mUsernameTextView.text = data.username
    }

    fun clickStream(): Observable<UsernameData> {
        return mLayout.rx_click().map { data }
    }
}
