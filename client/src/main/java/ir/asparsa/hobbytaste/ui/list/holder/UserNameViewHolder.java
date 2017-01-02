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
import rx.Observer;

/**
 * Created by hadi on 12/14/2016 AD.
 */
public class UserNameViewHolder extends BaseViewHolder<UsernameData> {

    @BindView(R.id.username)
    TextView mUsernameTextView;

    public UserNameViewHolder(
            View itemView,
            Observer<BaseRecyclerFragment.Event> observer,
            Bundle savedInstanceState
    ) {
        super(itemView, observer, savedInstanceState);
        ButterKnife.bind(this, itemView);
    }

    @Override public void onBindView(final UsernameData data) {
        mUsernameTextView.setText(data.getUsername());
        mUsernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mObserver.onNext(new UsernameClick(data.getUsername()));
            }
        });
    }

    public static class UsernameClick implements BaseRecyclerFragment.Event {

        private String username;

        public UsernameClick(
                String username
        ) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }
    }

}
