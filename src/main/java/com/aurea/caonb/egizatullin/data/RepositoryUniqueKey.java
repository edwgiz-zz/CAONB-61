package com.aurea.caonb.egizatullin.data;

public class RepositoryUniqueKey {

    private final String owner;
    private final String repo;
    private final String commitHash;

    public RepositoryUniqueKey(String owner, String repo, String commitHash) {
        this.owner = owner;
        this.repo = repo;
        this.commitHash = commitHash;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RepositoryUniqueKey that = (RepositoryUniqueKey) o;

        if (!owner.equals(that.owner)) {
            return false;
        }
        //noinspection SimplifiableIfStatement
        if (!repo.equals(that.repo)) {
            return false;
        }
        return commitHash.equals(that.commitHash);
    }

    @Override
    public int hashCode() {
        int result = owner.hashCode();
        result = 31 * result + repo.hashCode();
        result = 31 * result + commitHash.hashCode();
        return result;
    }
}
