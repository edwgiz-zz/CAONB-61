package com.aurea.caonb.egizatullin.controllers.commons;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.ResponseEntity.status;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class AbstractController {

    protected static final String HTTP_INTERNAL_ERROR_MESSAGE = "Internal Server Error";

    private static final Logger LOG = getLogger(AbstractController.class);

    @CrossOrigin(allowedHeaders = "CrossOrigin", origins = "*")
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AbstractResponse> exception(Exception e) {
        LOG.error(e.getMessage(), e);
        return status(HTTP_INTERNAL_ERROR).body(new ErrorResponse(
            defaultString(e.getMessage(), HTTP_INTERNAL_ERROR_MESSAGE)));
    }
}