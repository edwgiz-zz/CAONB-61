package com.aurea.caonb.egizatullin.controllers.impls.repository;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Reference to github repository. "
    + "Corresponds to url: https://api.github.com/repos/<owner>/<repo>")
public class AddRepositoryRequest {

    @ApiModelProperty(
        required = true,
        example = "edwgiz",
        notes = "Owner of github repository")
    private String owner;

    @ApiModelProperty(
        required = true,
        example = "CAONB-61",
        notes = "Name of github repository")
    private String repo;

    @ApiModelProperty(
        example = "master",
        notes = "Default value will be obtained from repository",
        position = 1)
    private String branch;
    @ApiModelProperty(
        dataType = "HEX string",
        example = "0ece1608f4f2c7db1bc9f0562b0fd744324a3395",
        notes = "Can not be used without 'branch'. Default is last commit",
        position = 2)
    private String commitHash;

    public String getOwner() {
        return owner;
    }

    public String getRepo() {
        return repo;
    }

    public String getBranch() {
        return branch;
    }

    public String getCommitHash() {
        return commitHash;
    }
}
