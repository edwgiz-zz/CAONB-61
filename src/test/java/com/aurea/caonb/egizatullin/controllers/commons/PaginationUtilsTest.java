package com.aurea.caonb.egizatullin.controllers.commons;

import static com.aurea.caonb.egizatullin.controllers.commons.pagination.PaginationUtils.DEFAULT_PAGE;
import static com.aurea.caonb.egizatullin.controllers.commons.pagination.PaginationUtils.DEFAULT_PER_PAGE;
import static com.aurea.caonb.egizatullin.controllers.commons.pagination.PaginationUtils.parse;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.aurea.caonb.egizatullin.controllers.commons.pagination.Pagination;
import org.junit.Test;


public class PaginationUtilsTest {

    @Test
    public void testToResponseHeader() throws Exception {

    }

    @Test
    public void testParse() throws Exception {
        assertPagination(parse(null, null), DEFAULT_PAGE, DEFAULT_PER_PAGE);
        assertPagination(parse(EMPTY, EMPTY), DEFAULT_PAGE, DEFAULT_PER_PAGE);
        assertPagination(parse("1", "30"), 1, 30);
        assertFail("'page' param  can't be lesser than 1: -1", "-1", "30");
        assertFail("'per_page' param  can't be lesser than 1: -10", "2", "-10");
        assertFail("Incorrect number in the 'page' param: blah", "blah", "30");
        assertFail("Incorrect number in the 'per_page' param: blah2", "2", "blah2");
    }

    private void assertFail(String expectedExceptionMessage, String page, String perPage) {
        try {
            parse(page, perPage);
            fail("No expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(expectedExceptionMessage, ex.getMessage());
        }
    }

    private void assertPagination(Pagination p, int expectedPage, int expectedPerPage) {
        assertEquals(expectedPage, p.page);
        assertEquals(expectedPerPage, p.perPage);
    }
}