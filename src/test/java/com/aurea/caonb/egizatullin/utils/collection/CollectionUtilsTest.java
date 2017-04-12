package com.aurea.caonb.egizatullin.utils.collection;

import static com.aurea.caonb.egizatullin.utils.collection.CollectionUtils.subList;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;


public class CollectionUtilsTest {


    @Test
    public void testSubList() throws Exception {
        List<Integer> fullList = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        testSubList(fullList, 0, 10, asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        testSubList(fullList, 10, 10, asList(11, 12, 13, 14, 15));
        testSubList(fullList, 20, 10, emptyList());
        testSubList(fullList, 0, 7, asList(1, 2, 3, 4, 5, 6, 7));
        testSubList(fullList, 7, 7, asList(8, 9, 10, 11, 12, 13, 14));
        testSubList(fullList, 14, 7, singletonList(15));

    }

    private void testSubList(List<Integer> fullList, int offset, int length, List<Integer> expected) {
        SubListResult<Integer> actual = subList(fullList.stream(), offset, length);
        assertEquals(fullList.size(), actual.totalSize);
        assertEquals(expected,
            actual.items);
    }
}