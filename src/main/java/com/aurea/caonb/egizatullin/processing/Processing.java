package com.aurea.caonb.egizatullin.processing;


import static com.aurea.caonb.egizatullin.und.commons.CodeInspectionType.UNUSED_FIELD;
import static com.aurea.caonb.egizatullin.und.commons.CodeInspectionType.UNUSED_METHOD;
import static com.aurea.caonb.egizatullin.und.commons.CodeInspectionType.UNUSED_PARAMETER;
import static org.slf4j.LoggerFactory.getLogger;

import com.aurea.caonb.egizatullin.data.InspectionDao;
import com.aurea.caonb.egizatullin.data.Repository;
import com.aurea.caonb.egizatullin.data.RepositoryDao;
import com.aurea.caonb.egizatullin.data.RepositoryState;
import com.aurea.caonb.egizatullin.data.RepositoryUniqueKey;
import com.aurea.caonb.egizatullin.und.UndService;
import com.aurea.caonb.egizatullin.utils.github.GithubService;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;

class Processing implements Runnable {

    private static final Logger LOG = getLogger(Processing.class);

    private final GithubService githubService;
    private final RepositoryDao repositoryDao;
    private final InspectionDao inspectionDao;
    private final Repository r;
    private final UndService undService;

    Processing(
        GithubService githubService,
        RepositoryDao repositoryDao,
        InspectionDao inspectionDao,
        Repository r,
        UndService undService) {
        this.repositoryDao = repositoryDao;
        this.githubService = githubService;
        this.inspectionDao = inspectionDao;
        this.r = r;
        this.undService = undService;
    }

    @Override
    public void run() {
        RepositoryUniqueKey ruk = new RepositoryUniqueKey(r.owner, r.repo, r.commitHash);
        try {
            repositoryDao.changeState(ruk, RepositoryState.PROCESSING, null);
            ArrayList<CodeInspectionItem> inspectionItems = inspectCode();
            inspectionDao.addInspections(r.id, inspectionItems);
            repositoryDao.changeState(ruk, RepositoryState.COMPLETED, null);
        } catch (Exception ex) {
            repositoryDao.changeState(ruk, RepositoryState.FAILED, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }
    }

    ArrayList<CodeInspectionItem> inspectCode() {
        Path projectDir = githubService.download(r.owner, r.repo, r.commitHash);
        if (LOG.isInfoEnabled()) {
            LOG.info("Guthub downloading completed: " + projectDir);
        }

        undService.buildDatabase(projectDir);
        LOG.info("'Understand' database building completed");
        ArrayList<CodeInspectionItem> inspectionItems = new ArrayList<>();
        int projectDirDepth = projectDir.getNameCount();
        undService.inspectCode(projectDir, Arrays.asList(
            new CodeInspectionCollector(projectDirDepth, UNUSED_METHOD, inspectionItems),
            new CodeInspectionCollector(projectDirDepth, UNUSED_FIELD, inspectionItems),
            new CodeInspectionCollector(projectDirDepth, UNUSED_PARAMETER, inspectionItems)
        ));
        LOG.info("'Understand' code inspection completed");
        return inspectionItems;
    }
}
