package com.overduefree.common;

import java.util.List;

public class PageResult<T> {

    private final List<T> list;
    private final int page;
    private final int pageSize;
    private final long total;

    public PageResult(List<T> list, int page, int pageSize, long total) {
        this.list = list;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }

    public List<T> getList() { return list; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public long getTotal() { return total; }
}
