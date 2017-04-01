package com.aurea.caonb.egizatullin.data;


import static com.aurea.caonb.egizatullin.data.RepositoryState.ADDED;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Repository
public class RepositoryDao {

    private final InspectionDao inspectionDao;
    private final AtomicInteger idSequence;
    private final ConcurrentHashMap<RepositoryUniqueKey, Repository> repositories;
    private final ConcurrentHashMap<Integer, RepositoryUniqueKey> repositoryKeys;

    @Autowired
    public RepositoryDao(InspectionDao inspectionDao) {
        this.inspectionDao = inspectionDao;
        idSequence = new AtomicInteger();
        repositories = new ConcurrentHashMap<>();
        repositoryKeys = new ConcurrentHashMap<>();
    }

    /**
     * @param owner Owner of git repository
     * @param repo Repo of git repository
     * @param branch branch, can be {@code null}
     * @param commitHash commit hash, can be {@code null}
     * @return repository instance and flag if it was just created
     */
    public AddRepositoryResult addRepository(String owner, String repo, String branch, String commitHash) {
        int id = idSequence.getAndIncrement();
        Repository newRepository = new Repository(
            id,
            owner, repo, branch, commitHash,
            unmodifiableList(singletonList(
                new RepositoryHistoryItem(new Date(), ADDED, null))));

        RepositoryUniqueKey key = new RepositoryUniqueKey(owner, repo, commitHash);
        Repository r = repositories.computeIfAbsent(
            key,
            k -> newRepository);
        repositoryKeys.put(id, key);
        return new AddRepositoryResult(r, r != newRepository);
    }


    public Repository remove(int id) {
        RepositoryUniqueKey key = repositoryKeys.remove(id);
        if(key == null) {
            return null;
        }
        inspectionDao.removeInspections(id);
        return repositories.remove(key);
    }


    public void changeState(RepositoryUniqueKey key, RepositoryState state, String message) {
        repositories.computeIfPresent(key, (k, old) -> {
            RepositoryState oldState = old.history.get(old.history.size() - 1).state;
            if (oldState.ordinal() > state.ordinal()) {
                throw new IllegalStateException(
                    "Can't change state from " + oldState + " to " + state);
            }
            List<RepositoryHistoryItem> his = new ArrayList<>();
            his.addAll(old.history);
            his.add(new RepositoryHistoryItem(new Date(), state, message));
            return new Repository(old.id, old.owner, old.repo, old.branch, old.commitHash,
                unmodifiableList(his));
        });
    }

    public List<Repository> find(String owner, String repo, String branch) {
        Stream<Repository> repos = repositories.entrySet().stream()
            .map(Entry::getValue);
        if (owner != null) {
            repos = repos.filter(e -> e.owner.contains(owner));
        }
        if (repo != null) {
            repos = repos.filter(e -> e.repo.contains(repo));
        }
        if (branch != null) {
            repos = repos.filter(e -> e.branch.contains(branch));
        }
        return repos
            .sorted(comparing(e -> e.id))
            .collect(toList());
    }
}