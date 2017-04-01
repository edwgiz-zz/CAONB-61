package com.aurea.caonb.egizatullin.controllers.impls.repository;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.springframework.http.ResponseEntity.status;

import com.aurea.caonb.egizatullin.controllers.commons.AbstractController;
import com.aurea.caonb.egizatullin.controllers.commons.AbstractResponse;
import com.aurea.caonb.egizatullin.data.AddRepositoryResult;
import com.aurea.caonb.egizatullin.data.Repository;
import com.aurea.caonb.egizatullin.data.RepositoryDao;
import com.aurea.caonb.egizatullin.processing.ProcessingService;
import com.aurea.caonb.egizatullin.utils.github.GithubService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/repositories", produces = "application/json")
public class RepositoryController extends AbstractController {

    private static final Pattern BRANCH_ANTI_PATTERN = Pattern.compile("\\s");
    private static final Pattern COMMIT_HASH_PATTERN = Pattern.compile("[0-9A-Fa-f]{7,40}");

    private final GithubService githubService;
    private final RepositoryDao repositoryDao;
    private final ProcessingService processingService;

    @Autowired
    public RepositoryController(
        GithubService githubService,
        RepositoryDao repositoryDao,
        ProcessingService processingService) {
        this.githubService = githubService;
        this.repositoryDao = repositoryDao;
        this.processingService = processingService;
    }

    @ApiOperation(
        value = "Add a Github repository",
        notes = "Adding a repository will automatically trigger an algorithm to detect dead code in"
            + " that repository.",
        tags = {"github"})
    @ApiResponses({
        @ApiResponse(code = HTTP_OK, message = "Nice!", response = AddRepositoryResponse.class),
        @ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = AddRepositoryResponse.class),
        @ApiResponse(code = HTTP_INTERNAL_ERROR, message = HTTP_INTERNAL_ERROR_MESSAGE, response = AbstractResponse.class)})
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<AddRepositoryResponse> addRepository(
        @ApiParam(value = "Reference to Github", required = true)
        @RequestBody AddRepositoryRequest req) {

        try {
            String owner = trimToNull(req.getOwner());
            if (owner == null) {
                return badAddRequest("Undefined github owner");
            }
            String repo = trimToNull(req.getRepo());
            if (repo == null) {
                return badAddRequest("Undefined github repository name");
            }
            String branch = trimToNull(req.getBranch());
            if (branch != null) {
                if (BRANCH_ANTI_PATTERN.matcher(branch).matches()) {
                    return badAddRequest("Incorrect branch");
                }
            } else {
                branch = githubService.getDefaultBranch(owner, repo); // validate git repository
            }

            String commitHash = trimToNull(req.getCommitHash());
            if (commitHash != null && !COMMIT_HASH_PATTERN.matcher(commitHash).matches()) {
                return badAddRequest("Incorrect commitHash");
            }
            if (commitHash == null) {
                commitHash = githubService.getLastCommit(owner, repo, branch);
            } else {
                if (isBlank(req.getBranch())) {
                    return badAddRequest("commitHash is not allowed without branch");
                }
            }

            AddRepositoryResult arr = repositoryDao.addRepository(owner, repo, branch, commitHash);
            Repository r = arr.repository;

            long lastModified = r.history.get(r.history.size() - 1).date.getTime();
            if (!arr.exists) {
                processingService.process(arr.repository);
                return status(HTTP_OK).lastModified(lastModified).body(
                    new AddRepositoryResponse(null, r));
            } else {
                return status(HTTP_BAD_REQUEST).lastModified(lastModified).body(
                    new AddRepositoryResponse("Github reference was already added", r));
            }
        } catch (Exception e) {
            return badAddRequest("Incorrect url: " + e.getMessage());
        }
    }

    private ResponseEntity<AddRepositoryResponse> badAddRequest(String msg) {
        return status(HTTP_BAD_REQUEST).body(new AddRepositoryResponse(msg, null));
    }


    @ApiOperation(
        value = "Deletes repository in the system",
        tags = {"github"})
    @ApiResponses({
        @ApiResponse(code = HTTP_OK, message = "Nice!", response = DeleteRepositoryResponse.class),
        @ApiResponse(code = HTTP_BAD_REQUEST, message = "Bad request", response = AbstractResponse.class),
        @ApiResponse(code = HTTP_INTERNAL_ERROR, message = HTTP_INTERNAL_ERROR_MESSAGE, response = AbstractResponse.class)})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<DeleteRepositoryResponse> remove(
        @ApiParam(value = "Identifier of repository ", required = true)
        @PathVariable("id") String repositoryId) {
        int id;
        try {
            id = Integer.parseInt(repositoryId);
        } catch (NumberFormatException ex) {
            return status(HTTP_BAD_REQUEST).body(new DeleteRepositoryResponse(
                "Incorrect repository id", null));
        }

        Repository deletedRepo = repositoryDao.remove(id);
        if (deletedRepo == null) {
            return status(HTTP_BAD_REQUEST).body(new DeleteRepositoryResponse(
                "No such repository id", null));
        } else {
            return status(HTTP_OK).body(new DeleteRepositoryResponse(
                null, deletedRepo));
        }
    }


    @ApiOperation(
        value = "List added repositories",
        notes = "List all repositories that have already been added along with their history.",
        tags = {"github"})
    @ApiResponses({
        @ApiResponse(code = HTTP_OK, message = "Nice!", response = Repository[].class)
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Repository>> list() {
        return status(HTTP_OK).body(repositoryDao.getAll());
    }
}