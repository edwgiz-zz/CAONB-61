package com.aurea.caonb.egizatullin.controllers.commons.pagination;

public class Pagination {

    public int page = PaginationUtils.DEFAULT_PAGE;
    public int perPage = PaginationUtils.DEFAULT_PER_PAGE;
    public int count = 0;
    public StringBuilder queryBase = new StringBuilder();

}
