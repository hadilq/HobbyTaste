package ir.asparsa.hobbytaste.ui.list.holder;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.core.util.PersianCalendar;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.core.util.LanguageUtil;
import ir.asparsa.hobbytaste.ui.list.data.CommentData;
import rx.Observer;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by hadi on 12/22/2016 AD.
 */
public class CommentViewHolder extends BaseViewHolder<CommentData> {

    @BindView(R.id.comment)
    TextView mCommentTextView;
    @BindView(R.id.heart)
    ImageView mHeartImageView;
    @BindView(R.id.datetime)
    TextView mDateTimeTextView;
    @BindView(R.id.rate)
    TextView mRateTextView;

    public CommentViewHolder(
            View itemView,
            Observer<BaseRecyclerFragment.Event> observer,
            Bundle savedInstanceState
    ) {
        super(itemView, observer, savedInstanceState);
        ButterKnife.bind(this, itemView);
    }

    @Override public void onBindView(final CommentData data) {
        mCommentTextView.setText(data.getCommentModel().getDescription());

        mRateTextView.setText(itemView.getContext().getResources().getQuantityString(
                R.plurals.like, (int) data.getCommentModel().getRate(), data.getCommentModel().getRate()));

        mDateTimeTextView.setText(formatDatetime(data.getCommentModel().getCreated(), itemView.getContext()));

        mHeartImageView.getDrawable().mutate().setColorFilter(
                itemView.getResources()
                        .getColor(data.getCommentModel().isLiked() ? R.color.heart_on : R.color.heart_off),
                PorterDuff.Mode.SRC_ATOP);
        mHeartImageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (mObserver != null) {
                    mObserver.onNext(new OnHeartClick(data));
                }
            }
        });
    }

    private String formatDatetime(
            long datetime,
            Context context
    ) {
        if (LanguageUtil.LANGUAGE_EN.equals(Locale.getDefault().getLanguage())) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(datetime);
            return formatCalendar(calendar, context);
        }
        PersianCalendar calendar = new PersianCalendar();
        calendar.setTimeInMillis(datetime);
        return formatCalendar(calendar, context);
    }

    private String formatCalendar(
            Calendar calendar,
            Context context
    ) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (LanguageUtil.LANGUAGE_EN.equals(Locale.getDefault().getLanguage())) {
            return String.format(Locale.getDefault(), "%d %s %d %d:%d", day,
                                 getMonthString(month, context), year, hour, minute);
        }
        return String.format(Locale.getDefault(), "%d %s %d %d:%d", day,
                             getMonthString(month, context), year, hour, minute);
    }

    private String getMonthString(
            int month,
            Context context
    ) {
        switch (month) {
            case 0:
                return context.getString(R.string.month_0);
            case 1:
                return context.getString(R.string.month_1);
            case 2:
                return context.getString(R.string.month_2);
            case 3:
                return context.getString(R.string.month_3);
            case 4:
                return context.getString(R.string.month_4);
            case 5:
                return context.getString(R.string.month_5);
            case 6:
                return context.getString(R.string.month_6);
            case 7:
                return context.getString(R.string.month_7);
            case 8:
                return context.getString(R.string.month_8);
            case 9:
                return context.getString(R.string.month_9);
            case 10:
                return context.getString(R.string.month_10);
            case 11:
                return context.getString(R.string.month_11);
        }
        return "";
    }

    public static class OnHeartClick implements BaseRecyclerFragment.Event {
        private CommentData comment;

        OnHeartClick(CommentData comment) {
            this.comment = comment;
        }

        public CommentData getComment() {
            return comment;
        }
    }
}
