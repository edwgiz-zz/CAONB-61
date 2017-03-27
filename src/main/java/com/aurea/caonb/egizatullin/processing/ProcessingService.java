package com.aurea.caonb.egizatullin.processing;


import static java.lang.Integer.MIN_VALUE;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.MINUTES;

import com.aurea.caonb.egizatullin.data.Repository;
import com.aurea.caonb.egizatullin.data.RepositoryDao;
import com.aurea.caonb.egizatullin.utils.github.GithubService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

@Service
public class ProcessingService implements Runnable, InitializingBean, DisposableBean {

    private static final Repository STOP_ITEM = new Repository(
        MIN_VALUE, null, null, null, null, null);

    private final GithubService githubService;
    private final RepositoryDao repositoryDao;

    private final ArrayBlockingQueue<Repository> queue;
    private final ExecutorService executorService;


    @Autowired
    public ProcessingService(
        GithubService githubService,
        RepositoryDao repositoryDao) {

        this.githubService = githubService;
        this.repositoryDao = repositoryDao;
        queue = new ArrayBlockingQueue<>(256);
        executorService = newFixedThreadPool(4,
            new CustomizableThreadFactory("repository-processing-"));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        executorService.submit(this);
    }

    @Override
    public void destroy() throws Exception {
        try {
            queue.put(STOP_ITEM);
        } catch (InterruptedException ignored) {
        }
        executorService.shutdown();
        executorService.awaitTermination(10, MINUTES);
    }

    public void process(Repository r) {
        try {
            queue.put(r);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        try {
            for (; ; ) {
                Repository r = queue.take();
                if (r == STOP_ITEM) {
                    break;
                }
                executorService.submit(new Processing(githubService, repositoryDao, r));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}