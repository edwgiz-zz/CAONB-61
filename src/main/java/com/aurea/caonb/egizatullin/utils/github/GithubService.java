package com.aurea.caonb.egizatullin.utils.github;


import static com.aurea.caonb.egizatullin.utils.file.FileUtils.getDirectory;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.file.Files.newOutputStream;

import com.aurea.caonb.egizatullin.utils.file.RecursiveDeleteFileVisitor;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GithubService {

    private final Path worktreeBaseDir;

    public GithubService(
        @Value("${app.workdir}") String workDir) {
        this.worktreeBaseDir = getDirectory(workDir, "github",
            "Can't download github repositories");
    }

    public String getDefaultBranch(String owner, String repo) {
        AbstractJsonGithubOperation<RepoResponse> op = new AbstractJsonGithubOperation<RepoResponse>(
            owner, repo,
            RepoResponse.class) {
            @Override
            void buildUrlFile(StringBuilder b) {
            }
        };
        callGithubApi(op);
        return op.getResponse().getDefaultBranch();
    }


    public Path download(String owner, String repo, String commit) {
        Mutable<Path> result = new MutableObject<>();
        callGithubApi(new AbstractGithubOperation(owner, repo) {
            @Override
            void buildUrlFile(StringBuilder b) {
                b.append("/tarball/");
                if (commit != null) {
                    b.append(commit);
                }
            }

            @Override
            public void onResponse(HttpURLConnection c) throws IOException {
                unarchive(new TarArchiveInputStream(
                    new GZIPInputStream(c.getInputStream(), 8096),
                    "UTF-8"));
            }

            private void unarchive(TarArchiveInputStream tarIs) {
                Path root = null;
                for (; ; ) {
                    try {
                        ArchiveEntry te = tarIs.getNextEntry(); // tar entry
                        if (te == null) {
                            break;
                        }
                        Path dst = worktreeBaseDir.resolve(te.getName());
                        if (te.isDirectory()) {
                            if (root == null) {
                                if (Files.exists(dst)) {
                                    Files.walkFileTree(dst, new RecursiveDeleteFileVisitor());
                                }
                                root = dst;
                            }
                            Files.createDirectory(dst);
                        } else {
                            try (OutputStream out = newOutputStream(dst,
                                StandardOpenOption.CREATE_NEW)) {
                                IOUtils.copy(tarIs, out);
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error during tar archive reading", e);
                    }
                }
                result.setValue(root);
            }
        });
        return result.getValue();
    }

    private void callGithubApi(AbstractGithubOperation op) {
        URL url;
        try {
            url = new URL("https", "api.github.com", op.buildUrlFile());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        try {
            HttpURLConnection c = (HttpsURLConnection) url.openConnection();
            try {
                c.setInstanceFollowRedirects(true);
                if (c.getResponseCode() == HTTP_OK) {
                    op.onResponse(c);
                } else {
                    throw new RuntimeException(
                        "IO fail with '" + url + "', HTTP response code: " + c.getResponseCode());
                }
            } finally {
                c.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException("IO fail with '" + url + "'", e);
        }
    }

    public String getLastCommit(String owner, String repo, String branch) {
        AbstractJsonGithubOperation<BranchResponse> op =
            new AbstractJsonGithubOperation<BranchResponse>(owner, repo, BranchResponse.class) {
                @Override
                void buildUrlFile(StringBuilder b) {
                    b.append("/branches/").append(urlEncode(branch));
                }
            };
        callGithubApi(op);
        return op.getResponse().getCommit().getSha();
    }
}
