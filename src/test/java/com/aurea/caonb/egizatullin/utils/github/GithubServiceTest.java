package com.aurea.caonb.egizatullin.utils.github;

import static com.aurea.caonb.egizatullin.utils.github.GitUtils.COMMIT_HASH_LENGTH;
import static java.nio.file.Files.walkFileTree;
import static org.junit.Assert.assertEquals;

import com.aurea.caonb.egizatullin.utils.file.RecursiveDeleteFileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;

public class GithubServiceTest {

    @Test
    public void test() throws Exception {
        Path tempDirectory = Files.createTempDirectory("github-service-test-");
        try {
            GithubService s = new GithubService(tempDirectory.toString());

            String defaultBranch = s.getDefaultBranch("edwgiz", "CAONB-61");
            assertEquals("master", defaultBranch);
            assertEquals(COMMIT_HASH_LENGTH, s.getLastCommit(
                "edwgiz", "CAONB-61", defaultBranch).length());
            Path repoDir = s.download(
                "edwgiz",
                "CAONB-61",
                "26a4e82a77afa0228a52198e3f02a88f71d50053");
            Path src = repoDir.resolve(
                "src/test/java/com/aurea/caonb/egizatullin/utils/github/GithubServiceTest.java");
            assertEquals(1164L, Files.size(src));
        } finally {
            walkFileTree(tempDirectory, new RecursiveDeleteFileVisitor());
        }
    }
}