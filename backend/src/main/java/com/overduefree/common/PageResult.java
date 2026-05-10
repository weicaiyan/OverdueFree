package com.overduefree.common;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class PageResult<T> {

    private final List<T> list;
    private final long page;
    private final long pageSize;
    private final long total;

    public PageResult(List<T> list, long page, long pageSize, long total) {
        this.list = Collections.unmodifiableList(list);
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }
}
