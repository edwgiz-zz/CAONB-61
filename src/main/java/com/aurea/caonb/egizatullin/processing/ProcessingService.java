package com.aurea.caonb.egizatullin.processing;


import static java.util.concurrent.TimeUnit.MINUTES;

import com.aurea.caonb.egizatullin.data.InspectionDao;
import com.aurea.caonb.egizatullin.data.Repository;
import com.aurea.caonb.egizatullin.data.RepositoryDao;
import com.aurea.caonb.egizatullin.und.UndService;
import com.aurea.caonb.egizatullin.utils.github.GithubService;
import com.scitools.understand.Understand;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

@Service
public class ProcessingService implements InitializingBean, DisposableBean {

    private final GithubService githubService;
    private final RepositoryDao repositoryDao;
    private final InspectionDao inspectionDao;
    private final UndService undService;
    private final ExecutorService executorService;


    @Autowired
    public ProcessingService(
        GithubService githubService,
        RepositoryDao repositoryDao,
        InspectionDao inspectionDao,
        UndService undService,
        @Value("${app.http.threadPool.maxThreads}") int queueSize) {
        this.githubService = githubService;
        this.repositoryDao = repositoryDao;
        this.inspectionDao = inspectionDao;
        this.undService = undService;
        executorService = new ThreadPoolExecutor(
            1, 1,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(queueSize),
            new CustomizableThreadFactory("repository-processing-"));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // library must be load in the same thread where the processings will be performed
        Future<?> f = executorService.submit(Understand::loadNativeLibrary);
        try {
            f.get();
        } catch (InterruptedException e) {
            throw new RuntimeException("Understand::loadNativeLibrary interruped", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Understand::loadNativeLibrary failed", e.getCause());
        }
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdown();
        executorService.awaitTermination(10, MINUTES);
    }

    public void process(Repository r) {
        executorService.submit(new Processing(githubService, repositoryDao,
            inspectionDao, undService, r));
    }
}