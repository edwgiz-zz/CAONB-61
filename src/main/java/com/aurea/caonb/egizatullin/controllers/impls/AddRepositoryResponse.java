package com.aurea.caonb.egizatullin.controllers.impls;

import com.aurea.caonb.egizatullin.controllers.commons.AbstractResponse;
import com.aurea.caonb.egizatullin.data.Repository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;


public class AddRepositoryResponse extends AbstractResponse {

    @ApiModelProperty(
        notes = "Created or existing repository")
    @JsonProperty
    private final Repository repository;

    AddRepositoryResponse(String errorMessage, Repository repository) {
        super(errorMessage);
        this.repository = repository;
    }
}