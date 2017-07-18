package ir.asparsa.android.ui.list.holder

import android.text.TextUtils
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import com.github.kittinunf.reactiveandroid.view.rx_click
import ir.asparsa.android.R2
import ir.asparsa.android.ui.list.data.TryAgainData
import ir.asparsa.android.ui.view.TryAgainView
import kotterknife.bindView
import rx.Observable

/**
 * @author hadi
 */
class TryAgainViewHolder(
        itemView: View
) : BaseViewHolder<TryAgainData>(itemView) {

    val mTryAgainView: TryAgainView by bindView(R2.id.try_again_view)

    init {
        ButterKnife.bind(this, itemView)
    }

    override fun onBindView(data: TryAgainData) {
        if (data.start()) {
            mTryAgainView.start()
        } else if (!TextUtils.isEmpty(data.errorMessage)) {
            mTryAgainView.onError(data.errorMessage)
        }
    }

    fun clickStream(): Observable<TryAgainData> {
        return mTryAgainView.rx_click().map { data }
    }
}
