package ir.asparsa.hobbytaste.ui.list.holder;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.asparsa.android.ui.fragment.recycler.BaseRecyclerFragment;
import ir.asparsa.android.ui.list.holder.BaseViewHolder;
import ir.asparsa.hobbytaste.R;
import ir.asparsa.hobbytaste.ui.list.data.UsernameData;

/**
 * Created by hadi on 12/14/2016 AD.
 */
public class UserNameViewHolder extends BaseViewHolder<UsernameData> {

    @BindView(R.id.username)
    TextView mUsernameTextView;

    public UserNameViewHolder(
            View itemView,
            BaseRecyclerFragment.OnEventListener onEventListener,
            Bundle savedInstanceState
    ) {
        super(itemView, onEventListener, savedInstanceState);
        ButterKnife.bind(this, itemView);
    }

    @Override public void onBindView(UsernameData data) {
        mUsernameTextView.setText(data.getUsername());
        mUsernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mOnEventListener.onEvent(EVENT_CLICK_USERNAME, null);
            }
        });
    }
}
