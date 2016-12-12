package ir.asparsa.common.net.dto;

import java.util.List;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class BaseListDto<T> {

    private boolean endOfList = false;
    private List<T> list;

    public BaseListDto(boolean endOfList, List<T> list) {
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
