package com.aurea.caonb.egizatullin.controllers.commons.pagination;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.ResponseEntity.BodyBuilder;

public class PaginationUtils {

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PER_PAGE = 30;
    public static final String PAGE_PARAM_NAME = "page";
    public static final String PER_PAGE_PARAM_NAME = "per_page";

    public static BodyBuilder paginationHeader(BodyBuilder bb, Pagination p) {
        return bb.header("Links", toResponseHeader(p));
    }

    private static String toResponseHeader(Pagination p) {
        StringBuilder b = defaultIfNull(p.queryBase, new StringBuilder(64));
        b.append(b.length() == 0 ? '?' : '&');
        if(p.perPage != DEFAULT_PER_PAGE) {
            b.append(PER_PAGE_PARAM_NAME).append('=').append(p.perPage).append('&');
        }
        int lastPage = 1 + p.count / p.perPage;
        ArrayList<Link> links = new ArrayList<>();
        links.add(link(b, DEFAULT_PAGE, "first"));
        if (p.page > 1 && p.page <= lastPage) {
            links.add(link(b, p.page - 1, "prev"));
        }
        if (p.page < lastPage) {
            links.add(link(b, p.page + 1, "next"));
        }
        links.add(link(b, lastPage, "last"));
        return new Links(links).toString();
    }

    private static Link link(StringBuilder b, int page, String rel) {
        int queryLength = b.length();
        b.append(PAGE_PARAM_NAME).append('=').append(page);
        Link link = new Link(b.toString(), rel);
        b.setLength(queryLength);
        return link;
    }

    public static Pagination parse(String page, String perPage) throws IllegalArgumentException {
        Pagination p = new Pagination();
        p.page = parseInt(PAGE_PARAM_NAME, page, DEFAULT_PAGE);
        p.perPage = parseInt(PER_PAGE_PARAM_NAME, perPage, DEFAULT_PER_PAGE);
        return p;
    }

    private static int parseInt(String name, String value, int defaultValue) {
        if (StringUtils.isNotBlank(value)) {
            try {
                int page = Integer.parseInt(value);
                if (page < 1) {
                    throw new IllegalArgumentException(
                        "'" + name + "' param  can't be lesser than 1: " + value);
                }
                return page;
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(
                    "Incorrect number in the '" + name + "' param: " + value);
            }
        }
        return defaultValue;
    }

    public static int getOffset(Pagination p) {
        return (p.page - 1) * p.perPage;
    }
}