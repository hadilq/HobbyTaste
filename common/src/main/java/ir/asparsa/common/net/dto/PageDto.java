package ir.asparsa.common.net.dto;

import java.util.List;

/**
 * @author hadi
 * @since 12/7/2016 AD
 */
public class PageDto<T> {

    private long totalElements;

    private List<T> list;

    public PageDto(
            long totalElements,
            List<T> list
    ) {
        this.totalElements = totalElements;
        this.list = list;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public List<T> getList() {
        return list;
    }
}
