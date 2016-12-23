package ir.asparsa.hobbytaste.ui.list.data;

import ir.asparsa.android.ui.list.data.BaseRecyclerData;
import ir.asparsa.hobbytaste.R;

/**
 * Created by hadi on 12/14/2016 AD.
 */
public class UsernameData extends BaseRecyclerData {

    public static final int VIEW_TYPE = R.layout.username;

    private String mUsername;

    public UsernameData(String username) {
        this.mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    @Override public int getViewType() {
        return VIEW_TYPE;
    }

    @Override public boolean equals(Object otherObj) {
        if ((otherObj == null) || !(otherObj instanceof UsernameData)) {
            return false;
        }
        final UsernameData other = (UsernameData) otherObj;
        return (getUsername() == null && other.getUsername() == null) ||
               (getUsername() != null && getUsername().equals(other.getUsername()));
    }
}
