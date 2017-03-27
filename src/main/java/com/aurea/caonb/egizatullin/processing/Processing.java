package com.aurea.caonb.egizatullin.processing;


import static org.slf4j.LoggerFactory.getLogger;

import com.aurea.caonb.egizatullin.data.Repository;
import com.aurea.caonb.egizatullin.data.RepositoryDao;
import com.aurea.caonb.egizatullin.data.RepositoryState;
import com.aurea.caonb.egizatullin.data.RepositoryUniqueKey;
import com.aurea.caonb.egizatullin.utils.github.GithubService;
import java.nio.file.Path;
import org.slf4j.Logger;

class Processing implements Runnable {

    private static final Logger LOG = getLogger(Processing.class);

    private final GithubService githubService;
    private final RepositoryDao repositoryDao;
    private final Repository r;

    Processing(
        GithubService githubService,
        RepositoryDao repositoryDao,
        Repository r
    ) {
        this.repositoryDao = repositoryDao;
        this.r = r;
        this.githubService = githubService;
    }

    @Override
    public void run() {
        RepositoryUniqueKey ruk = new RepositoryUniqueKey(r.owner, r.repo, r.commitHash);

        repositoryDao.changeState(ruk, RepositoryState.PROCESSING, null);
        try {
            process();

            repositoryDao.changeState(ruk, RepositoryState.COMPLETED, null);
        } catch (Exception ex) {
            repositoryDao.changeState(ruk, RepositoryState.FAILED, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }
    }

    private void process() {
        Path gitLocalDir = githubService.download(r.owner, r.repo, r.commitHash);
        if(LOG.isInfoEnabled()) {
            LOG.info("Guthub downloading completed: " + gitLocalDir);
        }
    }
}
