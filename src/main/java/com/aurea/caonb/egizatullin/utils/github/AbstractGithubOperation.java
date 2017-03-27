package com.aurea.caonb.egizatullin.utils.github;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

abstract class AbstractGithubOperation {

    private final String owner;
    private final String repo;

    AbstractGithubOperation(String owner, String repo) {
        this.owner = owner;
        this.repo = repo;
    }

    final String buildUrlFile() {
        StringBuilder b = new StringBuilder(64)
            .append("/repos/")
            .append(urlEncode(owner))
            .append('/')
            .append(urlEncode(repo));
        buildUrlFile(b);
        return b.toString();
    }

    String urlEncode(String owner) {
        try {
            return URLEncoder.encode(owner, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    abstract void buildUrlFile(StringBuilder b);

    abstract void onResponse(HttpURLConnection c) throws IOException;
}
