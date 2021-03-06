package ir.asparsa.hobbytaste.ui.list.holder;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.list.data.AboutUsData;
import rx.functions.Action1;

/**
 * @author hadi
 * @since 3/16/2017 AD.
 */
public class AboutUsViewHolder extends BaseViewHolder<AboutUsData> {

    @BindView(R.id.about_us_layout)
    ViewGroup mLayout;

    public AboutUsViewHolder(
            View itemView,
            Action1<BaseRecyclerFragment.Event> observer,
            Bundle savedInstanceState
    ) {
        super(itemView, observer, savedInstanceState);
        ButterKnife.bind(this, itemView);
    }

    @Override public void onBindView(final AboutUsData data) {
        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mObserver.call(new AboutUsClick());
            }
        });
    }

    public static class AboutUsClick implements BaseRecyclerFragment.Event {
        AboutUsClick() {
        }
    }

}
