package com.aurea.caonb.egizatullin.controllers.commons;


import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbstractResponse {

    @JsonProperty
    private final String errorMessage;


    protected AbstractResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}