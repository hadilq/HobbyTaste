package ir.asparsa.hobbytaste.ui.list.holder

import android.content.Context
import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.ButterKnife
import com.github.kittinunf.reactiveandroid.view.rx_click
import ir.asparsa.android.core.util.PersianCalendar
import ir.asparsa.android.ui.list.holder.BaseViewHolder
import ir.asparsa.hobbytaste.R
import ir.asparsa.hobbytaste.core.util.LanguageUtil
import ir.asparsa.hobbytaste.ui.list.data.CommentData
import kotterknife.bindView
import rx.Observable
import java.util.*

/**
 * @author hadi
 */
class CommentViewHolder(
        itemView: View
) : BaseViewHolder<CommentData>(itemView) {

    val mCommentTextView: TextView by bindView(R.id.comment)
    val mCreatorTextView: TextView by bindView(R.id.creator)
    val mHeartImageView: ImageView by bindView(R.id.heart)
    val mDateTimeTextView: TextView by bindView(R.id.datetime)
    val mRateTextView: TextView by bindView(R.id.rate)
    val mRateTailImageView: ImageView by bindView(R.id.rate_tail)

    init {
        ButterKnife.bind(this, itemView)

        mRateTailImageView.drawable.mutate()
                .setColorFilter(itemView.resources.getColor(R.color.heart_on), PorterDuff.Mode.SRC_ATOP)
    }

    override fun onBindView(data: CommentData) {
        super.onBindView(data)
        mCommentTextView.text = data.commentModel.description

        mCreatorTextView.text = data.commentModel.creator

        mRateTextView.text = String.format(Locale.getDefault(), "%d", data.commentModel.rate)

        mDateTimeTextView.text = formatDatetime(data.commentModel.created, itemView.context)

        mHeartImageView.drawable.mutate().setColorFilter(
                itemView.resources
                        .getColor(if (data.commentModel.isLiked) R.color.heart_on else R.color.heart_off),
                PorterDuff.Mode.SRC_ATOP)
    }

    private fun formatDatetime(
            datetime: Long,
            context: Context
    ): String {
        if (LanguageUtil.LANGUAGE_EN == Locale.getDefault().language) {
            val calendar = GregorianCalendar()
            calendar.timeInMillis = datetime
            return formatCalendar(calendar, context)
        }
        val calendar = PersianCalendar()
        calendar.timeInMillis = datetime
        return formatCalendar(calendar, context)
    }

    private fun formatCalendar(
            calendar: Calendar,
            context: Context
    ): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        if (LanguageUtil.LANGUAGE_EN == Locale.getDefault().language) {
            return String.format(Locale.getDefault(), "%d %s %d %d:%d", day,
                    getMonthString(month, context), year, hour, minute)
        }
        return String.format(Locale.getDefault(), "%d %s %d %d:%d", day,
                getMonthString(month, context), year, hour, minute)
    }

    private fun getMonthString(
            month: Int,
            context: Context
    ): String {
        when (month) {
            0 -> return context.getString(R.string.month_0)
            1 -> return context.getString(R.string.month_1)
            2 -> return context.getString(R.string.month_2)
            3 -> return context.getString(R.string.month_3)
            4 -> return context.getString(R.string.month_4)
            5 -> return context.getString(R.string.month_5)
            6 -> return context.getString(R.string.month_6)
            7 -> return context.getString(R.string.month_7)
            8 -> return context.getString(R.string.month_8)
            9 -> return context.getString(R.string.month_9)
            10 -> return context.getString(R.string.month_10)
            11 -> return context.getString(R.string.month_11)
        }
        return ""
    }

    fun clickStream(): Observable<CommentData> {
        return mHeartImageView.rx_click().map { data }
    }
}
