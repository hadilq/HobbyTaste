package ir.asparsa.android.core.model;

import java.util.List;

/**
 * @author hadi
 * @since 6/24/2016 AD
 */
public abstract class BaseListModel<T> {
    private boolean endOfList = false;
    private List<T> list;

    public BaseListModel(List<T> list) {
        this.list = list;
    }

    public BaseListModel(boolean endOfList, List<T> list) {
        this.endOfList = endOfList;
        this.list = list;
    }

    public boolean isEndOfList() {
        return endOfList;
    }

    public List<T> getList() {
        return list;
    }
}
