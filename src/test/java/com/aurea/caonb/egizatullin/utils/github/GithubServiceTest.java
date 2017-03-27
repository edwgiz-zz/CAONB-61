package com.aurea.caonb.egizatullin.utils.github;

import static java.nio.file.Files.walkFileTree;
import static org.junit.Assert.*;

import com.aurea.caonb.egizatullin.utils.file.RecursiveDeleteFileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.Test;

public class GithubServiceTest {

    @Test
    public void test() throws Exception {
        Path tempDirectory = Files.createTempDirectory("github-service-test-");
        try {
            GithubService s = new GithubService(tempDirectory.toString());

            String defaultBranch = s.getDefaultBranch("edwgiz", "CAONB-61");
            assertEquals("master", defaultBranch);
            String lastCommit = s.getLastCommit("edwgiz", "CAONB-61", defaultBranch);
            s.download("edwgiz", "CAONB-61", lastCommit);
            Path src = tempDirectory.resolve("src/test" + getClass().getName().replace('.', '/') + ".java");
            assertTrue(Files.isRegularFile(src));
        } finally {
            walkFileTree(tempDirectory, new RecursiveDeleteFileVisitor());
        }
    }
}