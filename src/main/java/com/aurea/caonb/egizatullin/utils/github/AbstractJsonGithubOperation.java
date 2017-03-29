package com.aurea.caonb.egizatullin.utils.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

abstract class AbstractJsonGithubOperation<T> extends AbstractGithubOperation {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final Class<T> responseClass;
    private T response;

    AbstractJsonGithubOperation(String owner, String repo, Class<T> responseClass) {
        super(owner, repo);
        this.responseClass = responseClass;
    }


    @Override
    void onResponse(HttpURLConnection c) throws IOException {
        InputStream is = c.getInputStream();
        response = JSON.readValue(is, responseClass);
    }

    T getResponse() {
        return response;
    }
}
