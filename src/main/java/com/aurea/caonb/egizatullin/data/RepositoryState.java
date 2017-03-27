package com.aurea.caonb.egizatullin.data;


public enum RepositoryState {

    ADDED("didn't started processing yet – this should fairly quick"),
    PROCESSING("processing is in progress"),
    COMPLETED("successfully"),
    FAILED("some error has occurred, exceptions as well");

    public final String description;

    RepositoryState(String description) {
        this.description = description;
    }
}
