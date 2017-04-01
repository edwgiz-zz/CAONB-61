package com.aurea.caonb.egizatullin.controllers.impls.repository;

import com.aurea.caonb.egizatullin.controllers.commons.AbstractResponse;
import com.aurea.caonb.egizatullin.data.Repository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;


public class DeleteRepositoryResponse extends AbstractResponse {

    @ApiModelProperty(
        notes = "Deleted repository")
    @JsonProperty
    private final Repository repository;

    DeleteRepositoryResponse(String errorMessage, Repository repository) {
        super(errorMessage);
        this.repository = repository;
    }
}