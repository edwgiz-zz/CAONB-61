package com.aurea.caonb.egizatullin.data;

public class AddRepositoryResult {

    /**
     * New or exists repository instance
     */
    public final Repository repository;
    /**
     * Repository already exists
     */
    public final boolean exists;

    AddRepositoryResult(Repository repository, boolean exists) {
        this.repository = repository;
        this.exists = exists;
    }
}
