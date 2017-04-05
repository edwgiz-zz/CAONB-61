package com.aurea.caonb.egizatullin.controllers.impls.repository;

import com.aurea.caonb.egizatullin.controllers.commons.AbstractResponse;
import com.aurea.caonb.egizatullin.data.Repository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;


public class ListRepositoryResponse extends AbstractResponse {

    @ApiModelProperty(notes = "Created or existing repository")
    @JsonProperty
    private final List<Repository> repositories;

    ListRepositoryResponse(String errorMessage, List<Repository> repositories) {
        super(errorMessage);
        this.repositories = repositories;
    }
}