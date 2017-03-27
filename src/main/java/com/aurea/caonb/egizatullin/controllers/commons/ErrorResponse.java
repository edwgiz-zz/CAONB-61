package com.aurea.caonb.egizatullin.controllers.commons;


public class ErrorResponse extends AbstractResponse {

    ErrorResponse(String errorMessage) {
        super(errorMessage);
    }
}