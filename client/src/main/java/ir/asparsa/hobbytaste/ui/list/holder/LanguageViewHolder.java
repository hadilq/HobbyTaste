package ir.asparsa.hobbytaste.ui.list.holder;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.list.data.LanguageData;
import rx.Observer;

/**
 * Created by hadi on 1/13/2017 AD.
 */
public class LanguageViewHolder extends BaseViewHolder<LanguageData> {

    @BindView(R.id.language)
    TextView mLanguageTextView;

    public LanguageViewHolder(
            View itemView,
            Observer<BaseRecyclerFragment.Event> observer,
            Bundle savedInstanceState
    ) {
        super(itemView, observer, savedInstanceState);
        ButterKnife.bind(this, itemView);
    }

    @Override public void onBindView(final LanguageData data) {
        mLanguageTextView.setText(data.getLanguage());
        mLanguageTextView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mObserver.onNext(new LanguageClick(data.getLangAbbreviation()));
            }
        });
    }

    public static class LanguageClick implements BaseRecyclerFragment.Event {

        private String langAbbreviation;

        LanguageClick(
                String langAbbreviation
        ) {
            this.langAbbreviation = langAbbreviation;
        }

        public String getLangAbbreviation() {
            return langAbbreviation;
        }
    }

}
