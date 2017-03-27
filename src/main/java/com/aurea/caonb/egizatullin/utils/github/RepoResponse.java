package com.aurea.caonb.egizatullin.utils.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
final class RepoResponse {

    @JsonProperty("default_branch")
    private String defaultBranch;

    String getDefaultBranch() {
        return defaultBranch;
    }
}
