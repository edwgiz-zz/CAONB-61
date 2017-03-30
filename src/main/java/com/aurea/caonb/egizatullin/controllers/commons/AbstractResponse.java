package com.aurea.caonb.egizatullin.controllers.commons;


public abstract class AbstractResponse {

    public final String errorMessage;


    protected AbstractResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}