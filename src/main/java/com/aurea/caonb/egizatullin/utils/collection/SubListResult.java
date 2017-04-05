package com.aurea.caonb.egizatullin.utils.collection;


import java.util.List;

public class SubListResult<T> {

    public final int totalSize;
    public final List<T> items;

    public SubListResult(int totalSize, List<T> items) {
        this.totalSize = totalSize;
        this.items = items;
    }
}
