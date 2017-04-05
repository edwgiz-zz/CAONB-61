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

        assertEquals(asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
            subList(fullList.stream(), 0, 10, fullList.size()));
        assertEquals(asList(11, 12, 13, 14, 15),
            subList(fullList.stream(), 10, 10, fullList.size()));
        assertEquals(emptyList(),
            subList(fullList.stream(), 20, 10, fullList.size()));

        assertEquals(asList(1, 2, 3, 4, 5, 6, 7),
            subList(fullList.stream(), 0, 7, fullList.size()));
        assertEquals(asList(8, 9, 10, 11, 12, 13, 14),
            subList(fullList.stream(), 7, 7, fullList.size()));
        assertEquals(singletonList(15),
            subList(fullList.stream(), 14, 7, fullList.size()));

    }
}