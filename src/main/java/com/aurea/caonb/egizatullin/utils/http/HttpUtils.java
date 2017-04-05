package com.aurea.caonb.egizatullin.utils.http;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.lang3.StringUtils;

public final class HttpUtils {

    public static StringBuilder addParam(StringBuilder query, String name, int value) {
        return addParamDelimiter(query).append(name).append('=').append(value);
    }

    public static StringBuilder addParam(StringBuilder query, String name, String value) {
        if (StringUtils.isBlank(value)) {
            return query;
        }
        try {
            return addParamDelimiter(query).append(name).append('=')
                .append(URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private static StringBuilder addParamDelimiter(StringBuilder query) {
        return query.append(query.length() == 0 ? '?' : '&');
    }

    private HttpUtils() {
    }
}