package com.aurea.caonb.egizatullin.utils.collection;

import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

public final class CollectionUtils {

    public static <T> SubListResult<T> subList(Stream<T> stream, int offset, int length) {
        int i = 0;
        int limit = offset + length;
        ArrayList<T> items = new ArrayList<>(min(length, 1024));
        Iterator<T> it = stream.iterator();
        while (it.hasNext()) {
            T e = it.next();
            if (i >= offset && i < limit) {
                items.add(e);
            }
            i++;
        }
        return new SubListResult<>(i, items);
    }


    private CollectionUtils() {
    }
}