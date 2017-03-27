package com.aurea.caonb.egizatullin.utils.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BranchResponse {

    private Commit commit;

    public Commit getCommit() {
        return commit;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Commit {
        private String sha;

        public String getSha() {
            return sha;
        }
    }
}
